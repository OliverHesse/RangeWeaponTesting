package me.Lucent.Events

import me.Lucent.Mechanics.StatProfiles.WeaponStatProfile
import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.Entity
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

//TODO add some sort of DamageSource e.g PrimaryFire,Ability,PassiveAbility
class PlayerAttackEntityEvent(val plugin: RangedWeaponsTest,val playerWrapper: PlayerWrapper,val statProfile:WeaponStatProfile,val target:Entity) : Event() {

    var item:ItemStack? = null;
    var cancelled = false


    override fun getHandlers(): HandlerList {
        return HANDLERS
    }

    companion object {
        fun create(plugin: RangedWeaponsTest, playerWrapper: PlayerWrapper,item: ItemStack, target:Entity):PlayerAttackEntityEvent{
            val newEvent = PlayerAttackEntityEvent(plugin,playerWrapper, WeaponStatProfile(emptyMap(),0.0,0.0,0.0),target)
            newEvent.item = item
            return newEvent
        }

        private val HANDLERS = HandlerList()

        //I just added this.
        @JvmStatic
        fun getHandlerList(): HandlerList {
            return HANDLERS
        }
    }




}