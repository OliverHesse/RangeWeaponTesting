package me.Lucent


import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import me.Lucent.WeaponMechanics.Shooting.ActiveExecutors
import me.Lucent.WeaponMechanics.Shooting.FullAutoFireTask
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.File

class PlayerController(val plugin:RangedWeaponsTest):Listener  {


    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEvent){
        val wrappedPlayer = PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        val itemStack = wrappedPlayer.activeItemData.getItemStack()
        val id = itemStack.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return;
        plugin.logger.info("item id retrieved")
        //call executor
        val weaponData = YamlConfiguration.loadConfiguration(File(plugin.dataFolder,"/RangedWeaponData.yml")).getConfigurationSection(id) ?: return
        plugin.logger.info("weapon data retrieved")

        if(e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK){


            //player used an ability


            //weapon does not have an active slot
            if(!weaponData.getBoolean("hasActiveSlot")) return
            plugin.logger.info("item has active slot")

            if(wrappedPlayer.activeItemData.isAbilityOnCooldown()) return;
            plugin.logger.info("ability is not on cooldown")
            //get executor

            val chipId = itemStack.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"activeChip"), PersistentDataType.STRING)!!

            val chipData = YamlConfiguration.loadConfiguration(File(plugin.dataFolder,"/ActiveChips.yml")).getConfigurationSection(chipId) ?: return
            plugin.logger.info("got chip data")
            val executorName = chipData.getString("executor")
            ActiveExecutors.executorNameToFunction[executorName]!!.call(wrappedPlayer)

            //TODO start cooldown

            return;
        }
        if(e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return


        if(!wrappedPlayer.activeItemData.isCurrentlyFiring()){
            //logic here
            val executor = weaponData.getConfigurationSection("WeaponStats")?.getString("primaryFireExecutor") ?: return
            val args = weaponData.getConfigurationSection("WeaponStats")?.getList("executorArgs") ?: return


            ActiveExecutors.executorNameToFunction[executor]!!.call(wrappedPlayer, args.toTypedArray())

        }

        wrappedPlayer.rightClicked();
        e.isCancelled = true;
    }
    @EventHandler
    fun onMainHandItemChanged(e:PlayerItemHeldEvent){
        PlayerWrapperHolder.getPlayerWrapper(plugin,e.player).activeItemData.reset();
    }
    @EventHandler
    fun onProjectileLaunch(e:ProjectileLaunchEvent){
        plugin.logger.info("Projectile was launched")
    }
    @EventHandler
    fun onPlayerJoin(e:PlayerJoinEvent){
        e.player.inventory.clear()
        val tempLauncher = plugin.createTestExplosiveWeapon();
        e.player.inventory.addItem(tempLauncher)

        val tempRifle = ItemStack(Material.DIAMOND_SWORD,1)
        tempRifle.editMeta {
            it.persistentDataContainer.set(NamespacedKey(plugin,"fireRate"), PersistentDataType.DOUBLE,20.0);
            it.displayName(Component.text("Basic Assault Rifle"));
        }

        e.player.inventory.addItem(tempRifle);
    }
    @EventHandler
    fun onPlayerLeave(e:PlayerQuitEvent){
        PlayerWrapperHolder.removeWrapper(e.player);
    }

}