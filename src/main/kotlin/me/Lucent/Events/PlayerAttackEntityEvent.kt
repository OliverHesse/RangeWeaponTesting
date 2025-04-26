package me.Lucent.Events

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

//TODO add some sort of DamageSource e.g PrimaryFire,Ability,PassiveAbility
class PlayerAttackEntityEvent(val plugin: RangedWeaponsTest,val playerWrapper: PlayerWrapper,val item:ItemStack,val target:Entity) : Event() {

    var cancelled = false


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