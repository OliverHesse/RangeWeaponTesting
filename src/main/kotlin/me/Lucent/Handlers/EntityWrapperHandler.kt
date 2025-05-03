package me.Lucent.Handlers

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.EntityWrapper
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

class EntityWrapperHandler(val plugin:RangedWeaponsTest){
    private val entityWrappers:HashMap<Entity,EntityWrapper> = HashMap();

    fun getEntityWrapper( entity: Entity): EntityWrapper {
        if(entityWrappers.containsKey(entity)) return entityWrappers[entity]!!

        val wrapper = EntityWrapper(plugin,entity)
        entityWrappers[entity] = wrapper
        return wrapper

    }

    //for when a player leaves their data gets added into the queue to be saved
    fun addSaveTask(entity: Entity){}

    fun removeWrapper(entity: Entity){
        if(!entityWrappers.containsKey(entity)) return;

        addSaveTask(entity)
        entityWrappers[entity]!!.safeDeleteWrapper()
        entityWrappers.remove(entity);
    }
}