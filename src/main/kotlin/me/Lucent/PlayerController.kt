package me.Lucent


import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import me.Lucent.WeaponMechanics.Shooting.FullAutoFireTask
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class PlayerController(val plugin:RangedWeaponsTest):Listener  {


    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEvent){

        if(e.action == Action.LEFT_CLICK_AIR || e.action == Action.LEFT_CLICK_BLOCK){
            val fovChange:WrapperPlayServerPlayerAbilities = WrapperPlayServerPlayerAbilities(
                false,
                false,
                false,
                false,
                0.05f,
                -0.3f
            )

            PacketEvents.getAPI().playerManager.sendPacket(e.player,fovChange)

            return;
        }
        if(e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return
        plugin.logger.info("Attempting to shoot bullet")

        val wrappedPlayer = PlayerWrapperHolder.getPlayerWrapper(plugin,e.player);
        if(!wrappedPlayer.activeItemData.isCurrentlyFiring()){
            plugin.logger.info("creating full auto task")
            val task = FullAutoFireTask(plugin,wrappedPlayer);
            wrappedPlayer.activeItemData.fullAutoTask = task;
            task.runTaskTimer(plugin,0,1);
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
        val tempRifle = ItemStack(Material.DIAMOND_SWORD,1)
        tempRifle.editMeta {
            it.persistentDataContainer.set(NamespacedKey(plugin,"fireRate"), PersistentDataType.DOUBLE,20.0);
            it.displayName(Component.text("Basic Assault Rifle"));
        }
        e.player.inventory.clear()
        e.player.inventory.addItem(tempRifle);
    }

}