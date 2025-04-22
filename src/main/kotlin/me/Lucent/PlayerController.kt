package me.Lucent


import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import me.Lucent.WeaponMechanics.Reloading.ReloadTask
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
    fun primaryFireUsedEventCheck(e:PlayerInteractEvent){
        if(e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
        plugin.logger.info("player tried to use primary fire")
        val wrappedPlayer = PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        val itemStack = wrappedPlayer.activeItemData.getItemStack()

        val weaponData = wrappedPlayer.activeItemData.getWeaponYamlData() ?: return;



        if(wrappedPlayer.activeItemData.isReloading()) return


        if(!wrappedPlayer.activeItemData.isCurrentlyFiring()){
            //logic here
            val executor = weaponData.getConfigurationSection("WeaponStats")?.getString("primaryFireExecutor") ?: return
            val args = weaponData.getConfigurationSection("WeaponStats")?.getList("executorArgs") ?: listOf<Any>()


            val remainingAmmo = itemStack.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"ammoLeft"),
                PersistentDataType.INTEGER)
            if (remainingAmmo == 0){
                wrappedPlayer.activeItemData.reloadTask = ReloadTask(plugin,wrappedPlayer)
                wrappedPlayer.activeItemData.reloadTask!!.runTaskTimer(plugin,0,1);
                plugin.logger.info("RELOADING WEAPON")
                return
            }

            val status = ActiveExecutors.executorNameToFunction[executor]!!.call(plugin,wrappedPlayer, args.toTypedArray())
            plugin.logger.info(status.toString())
            //used for cooldown checks
            if(status) wrappedPlayer.activeItemData.weaponShot();
        }

        wrappedPlayer.rightClicked();
        e.isCancelled = true;
    }



    //might make 1 for abilities and 1 for primary fire
    @EventHandler
    fun abilityUsedEventCheck(e:PlayerInteractEvent){
        if(e.action != Action.LEFT_CLICK_AIR && e.action != Action.LEFT_CLICK_BLOCK) return
        plugin.logger.info("interact event called")
        val wrappedPlayer = PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);
        if(!wrappedPlayer.isItemEquip()) return;
        val itemStack = wrappedPlayer.activeItemData.getItemStack()

        val weaponData = wrappedPlayer.activeItemData.getWeaponYamlData() ?: return;
        plugin.logger.info("weapon data retrieved")

        //weapon does not have an active slot
        if(!weaponData.getBoolean("hasActiveSlot")) return
        plugin.logger.info("item has active slot")

        if(wrappedPlayer.activeItemData.isAbilityOnCooldown()) return;
        plugin.logger.info("ability is not on cooldown")
        //get executor

        val chipId = itemStack.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"activeChip"), PersistentDataType.STRING)!!

        val chipData = YamlConfiguration.loadConfiguration(File(plugin.dataFolder,"/ActiveChips.yml")).getConfigurationSection(chipId) ?: return
        plugin.logger.info("got chip data")
        val executor = chipData.getString("executor")
        val args = chipData.getList("executorArgs") ?: listOf<Any>()
        //TODO modify to add args
        val status = ActiveExecutors.executorNameToFunction[executor]!!.call(plugin,wrappedPlayer,args.toTypedArray())

        //used for cooldown checks
        if(status) wrappedPlayer.activeItemData.abilityUsed();




        e.isCancelled = true
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
        val tempLauncher = plugin.createCustomItem("BasicGrenadeLauncher");
        val tempRifle = plugin.createCustomItem("BasicAssaultRifle")
        e.player.inventory.addItem(tempLauncher)
        e.player.inventory.addItem(tempRifle)
    }
    @EventHandler
    fun onPlayerLeave(e:PlayerQuitEvent){
        PlayerWrapperHolder.removeWrapper(e.player);
    }

}