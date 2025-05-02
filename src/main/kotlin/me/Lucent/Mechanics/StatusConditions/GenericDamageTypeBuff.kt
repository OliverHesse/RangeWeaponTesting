package me.Lucent.Mechanics.StatusConditions

import me.Lucent.Enums.DamageBuffType
import me.Lucent.Enums.DamageType
import me.Lucent.Enums.StatusType
import me.Lucent.Events.CustomPlayerDamageEntityEvent
import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.event.EventHandler
import kotlin.math.min

class GenericDamageTypeBuff(val plugin:RangedWeaponsTest,val player:PlayerWrapper, val stat:DamageType,val type:DamageBuffType,val value:Double, val name:String, val duration:Double):
    StatusCondition() {
    val statusType = StatusType.Buff

    override var stackCount: Int = 1;
    override val maxStackCount: Int = 1;
    override fun addStacks(int: Int) {
        stackCount += min(stackCount+int,maxStackCount)

    }

    override fun reduceStacks(int: Int) {
        val newStackCount = stackCount - int

        if(newStackCount <= 0){
            remove()
        }
    }

    //used to call status manager
    fun remove(){
        plugin.logger.info("removing buff")
        player.playerStatusHandler.removeStatusCondition(name)
    }
    override fun destroy() {
        plugin.logger.info("Destroying buff $name")
        CustomPlayerDamageEntityEvent.getHandlerList().unregister(this)
    }

    fun startBuffTimer(){
        // "infinite" duration
        if(duration <= 0.0) return
        plugin.server.pluginManager.registerEvents(this,plugin)
        plugin.logger.info("${duration}")
        plugin.server.scheduler.runTaskLater(plugin,Runnable {
            plugin.logger.info("buff will be removed")
            this.remove()
        },(duration*20).toLong())

    }

    @EventHandler
    fun playerDamageEntityEvent(e:CustomPlayerDamageEntityEvent){
        when(type){
            DamageBuffType.Bonus -> {e.damageModifiers.baseDamageBonus[stat] =
                e.damageModifiers.baseDamageBonus[stat] ?: (0.0 + value)
            }
            DamageBuffType.FinalBonus -> {e.damageModifiers.finalDamageBonus[stat] =
                e.damageModifiers.finalDamageBonus[stat] ?: (0.0 + value)
            }
            DamageBuffType.Multiplier ->  {e.damageModifiers.damageMultipliers[stat] =
                e.damageModifiers.damageMultipliers[stat] ?: (0.0 + value)
            }
            DamageBuffType.FinalMultiplier -> {

                if(e.damageModifiers.finalDamageMultipliers.containsKey(stat)) e.damageModifiers.finalDamageMultipliers[stat] = mutableListOf(value)
                else e.damageModifiers.finalDamageMultipliers[stat]!!.add(value)

            }

        }
    }

}