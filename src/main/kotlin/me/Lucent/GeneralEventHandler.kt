package me.Lucent

import me.Lucent.Events.PlayerAttackEntityEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener


class GeneralEventHandler(val plugin: RangedWeaponsTest):Listener {

    @EventHandler
    fun onCustomPlayerAttackEntity(e:PlayerAttackEntityEvent){

        plugin.logger.info("player ${e.playerWrapper.player.name} attacked entity ${e.target.name}")
    }
}