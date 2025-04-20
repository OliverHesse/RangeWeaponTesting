package me.Lucent.Wrappers

import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.Shooting.ActiveExecutors
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler

class ActiveItemData(val plugin:RangedWeaponsTest,val player:PlayerWrapper){


    var fullAutoTask:BukkitRunnable? = null;
    var zoomedIn = false;
    var abilityCooldownTask:BukkitRunnable? = null;
    fun getItemStack(): ItemStack {
        return player.player.inventory.itemInMainHand;
    }

    //TODO change to just do time comparison
    fun isAbilityOnCooldown():Boolean{

        if(abilityCooldownTask  == null) return false;
        if(abilityCooldownTask!!.isCancelled) {abilityCooldownTask = null; return false};


        return  true

    }


    //checks if it is on full auto
    //TODO check if it is currently burst firing
    fun isCurrentlyFiring():Boolean{

        if(fullAutoTask == null) return false;

        plugin.logger.info("Is it auto: "+plugin.server.scheduler.isCurrentlyRunning(fullAutoTask!!.taskId).toString())
        if(fullAutoTask!!.isCancelled){
            fullAutoTask = null;
            return false
        }
        return true;
    }


    //checks if weapon is sill on cooldown(only really matters for singleshot)
    fun firingOnCooldown():Boolean{
        return false
    }

    fun isReloading():Boolean{return false}

    /**
     * clear all active tasks
     * if zoomed in reset
     */
    fun reset(){
        if(fullAutoTask!=null) fullAutoTask!!.cancel()
        fullAutoTask = null

        if(zoomedIn){
            ScopeHandler.zoomOut(player)
        }
    }
}