package me.Lucent.Events

import me.Lucent.Mechanics.StatProfiles.PlayerDamageEntityEventModifiers
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class CustomPlayerDamageEntityEvent(val player:PlayerWrapper,val target:Entity,val damageModifiers:PlayerDamageEntityEventModifiers): Event()  {
    override fun getHandlers(): HandlerList {
        return HANDLERS
    }
    companion object {
        private val HANDLERS = HandlerList()

        //I just added this.
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }
}