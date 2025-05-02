package me.Lucent.Events

import me.Lucent.Mechanics.StatusConditions.StatusCondition
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

class PlayerStatusConditionRemovedEvent(val player: PlayerWrapper, id:String, statusCondition:StatusCondition):Event(){
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