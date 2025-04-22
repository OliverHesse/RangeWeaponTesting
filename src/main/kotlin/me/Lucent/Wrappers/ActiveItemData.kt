package me.Lucent.Wrappers

import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.Shooting.ActiveExecutors
import net.kyori.adventure.util.Services.Fallback
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitScheduler
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ActiveItemData(val plugin:RangedWeaponsTest,val player:PlayerWrapper){


    var fullAutoTask:BukkitRunnable? = null;
    var zoomedIn = false;
    var abilityCooldownTask:BukkitRunnable? = null;
    var lastShotTime:Long = 0;
    var lastAbilityUsed:Long = 0;
    fun getItemStack(): ItemStack {
        return player.player.inventory.itemInMainHand;
    }

    //TODO change to just do time comparison
    fun isAbilityOnCooldown():Boolean{

        if(abilityCooldownTask  == null) return false;
        if(abilityCooldownTask!!.isCancelled) {abilityCooldownTask = null; return false};


        return  true

    }


    //assumed to be in seconds
    fun canAbilityBeUsed(cooldown: Double):Boolean{
        val timeElapsed:Duration = (System.currentTimeMillis()-lastAbilityUsed).milliseconds;
        return timeElapsed > cooldown.seconds

    }
    //assumed to be in seconds
    fun canWeaponShoot(cooldown:Double):Boolean{
        val timeElapsed:Duration = (System.currentTimeMillis()-lastShotTime).milliseconds;
        plugin.logger.info(timeElapsed.toString())
        plugin.logger.info((timeElapsed > cooldown.seconds).toString())
        return timeElapsed > cooldown.seconds
    }


    fun abilityUsed(){
        lastAbilityUsed = System.currentTimeMillis();
    }
    fun weaponShot(){
        lastShotTime = System.currentTimeMillis();
    }
    //checks if it is on full auto
    //TODO check if it is currently burst firing
    fun isCurrentlyFiring():Boolean{

        if(fullAutoTask == null) return false;

        plugin.logger.info("is task running: "+plugin.server.scheduler.isCurrentlyRunning(fullAutoTask!!.taskId).toString())
        if(fullAutoTask!!.isCancelled){
            fullAutoTask = null;
            return false
        }
        return true;
    }


    //checks if weapon is still on cooldown(only really matters for singleshot)
    fun firingOnCooldown():Boolean{
        return false
    }


    fun getWeaponYamlData():ConfigurationSection?{
        val id = getItemStack().itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return null;
        return YamlConfiguration.loadConfiguration(File(plugin.dataFolder,"/RangedWeaponData.yml")).getConfigurationSection(id) ?: return null
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

        lastShotTime = 0;
        lastAbilityUsed = 0;
    }



}