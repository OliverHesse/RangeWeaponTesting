package me.Lucent.Handlers

import me.Lucent.Events.PlayerStatusConditionRemovedEvent
import me.Lucent.RangedWeaponsTest
import me.Lucent.Mechanics.StatusConditions.StatusCondition
import me.Lucent.Wrappers.PlayerWrapper

class PlayerStatusHandler(val plugin: RangedWeaponsTest, val player:PlayerWrapper) {

    //string is the status ID. this can be either UUID+status-name or just status name.
    //this is so if an entity tries to apply the same buff or debuff twice it will either reapply it or stack it
    val statusMap = mutableMapOf<String,StatusCondition>()



    fun addStatusCondition(id:String,condition: StatusCondition){

        if(!statusMap.containsKey(id)) statusMap[id] = condition;
        else{
            if(statusMap[id]!!.stackCount < statusMap[id]!!.maxStackCount) statusMap[id]!!.addStacks(1)
        }
    }


    fun reduceStatusStackCount(id:String,int: Int){
        if(!statusMap.containsKey(id)) return

        statusMap[id]!!.reduceStacks(int)
    }

    fun removeStatusCondition(id:String){
        plugin.logger.info("removing buff1")
        val statusCondition = statusMap.remove(id) ?: return
        val event = PlayerStatusConditionRemovedEvent(player,id,statusCondition)
        event.callEvent();
        plugin.logger.info("removing buff2")
        statusCondition.destroy()
    }
}