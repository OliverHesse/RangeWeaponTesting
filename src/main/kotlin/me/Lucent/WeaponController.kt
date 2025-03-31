package me.Lucent


import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

class WeaponController(val plugin:Plugin):Listener  {


    @EventHandler
    fun onPlayerInteract(e:PlayerInteractEvent){
        if(e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return

        val fovChange:WrapperPlayServerPlayerAbilities = WrapperPlayServerPlayerAbilities(
            false,
            false,
            false,
            false,
            0.05f,
            0.3f
        )
        PacketEvents.getAPI().playerManager.sendPacket(e.player,fovChange)
    }


    @EventHandler
    fun onPlayerJoin(e:PlayerJoinEvent){
        val hoe = ItemStack(Material.DIAMOND_HOE,1);
        e.player.inventory.addItem(hoe);
    }

}