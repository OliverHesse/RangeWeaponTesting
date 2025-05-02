package me.Lucent

import me.Lucent.Events.CustomPlayerDamageEntityEvent
import me.Lucent.Events.PlayerAttackEntityEvent
import me.Lucent.Handlers.PlayerWrapperHandler
import me.Lucent.Mechanics.StatProfiles.PlayerDamageEntityEventModifiers
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent


class GeneralEventHandler(val plugin: RangedWeaponsTest):Listener {


    //mainly used to do all the stuff needed before the player damages the enemy. then calls the player damaged enemy event
    @EventHandler
    fun onCustomPlayerAttackEntity(e:PlayerAttackEntityEvent){

        plugin.logger.info("player ${e.playerWrapper.player.name} attacked entity ${e.target.name}")
        val eventModifiers = PlayerDamageEntityEventModifiers()

        //TODO add weapon modifiers

        val damageEvent = CustomPlayerDamageEntityEvent(e.playerWrapper,e.target, eventModifiers)
        damageEvent.callEvent()

        plugin.logger.info("${eventModifiers.baseDamageBonus}")
    }

    @EventHandler
    fun onProjectileHit(e:ProjectileHitEvent){
        if(e.entity.shooter !is Player) return

        if(e.hitEntity == null) return

        e.isCancelled = true
        val item =   plugin.projectileHandler.getItemStack(e.entity) ?: return
        val attackEvent = PlayerAttackEntityEvent(plugin,
            plugin.playerWrapperHandler.getPlayerWrapper(e.entity.shooter as Player),item,e.hitEntity!!)
        attackEvent.callEvent()
    }
}