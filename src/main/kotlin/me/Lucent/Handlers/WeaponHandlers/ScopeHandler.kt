package me.Lucent.Handlers.WeaponHandlers

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.GameMode
import org.bukkit.entity.Player

object ScopeHandler {


     fun zoomIn(player:PlayerWrapper,scopeZoom:Float){


         if (scopeZoom < 1.0 || scopeZoom > 10.0) return

         val fovChange: WrapperPlayServerPlayerAbilities = WrapperPlayServerPlayerAbilities(
             false,
             false,
             false,
             false,
             0.05f,
             (1f / (20f/ scopeZoom - 10f))
         )

         PacketEvents.getAPI().playerManager.sendPacket(player.player,fovChange)
         player.activeItemData.zoomedIn = true
     }

     fun zoomOut(player: PlayerWrapper){
         val fovChange: WrapperPlayServerPlayerAbilities = WrapperPlayServerPlayerAbilities(
             player.player.isInvisible,
             player.player.isFlying,
             player.player.allowFlight,
             player.player.gameMode == GameMode.CREATIVE,
             player.player.flySpeed,
             player.player.walkSpeed
         )

         PacketEvents.getAPI().playerManager.sendPacket(player.player,fovChange)
         player.activeItemData.zoomedIn = false
     }

     fun updateZoom(){

     }
}