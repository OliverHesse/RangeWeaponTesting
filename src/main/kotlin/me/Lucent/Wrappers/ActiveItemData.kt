package me.Lucent.Wrappers

import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.RangedWeaponsTest
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ActiveItemData(val plugin:RangedWeaponsTest,val player:PlayerWrapper){


    var fullAutoTask:RunnableWrapper? = null;
    var chargingTask:RunnableWrapper? = null;



    //string is weapon id Long is last used
    var activeChipCooldownTracker = mutableMapOf<String,Long>()
    var activeChipTaskTracker = mutableMapOf<String,Long>()
    var activeChipActiveTracker = mutableMapOf<String,Long>()


    //TODO remove and replace with abilityActive
    var zoomedIn = false;
    var abilityCooldownTask:RunnableWrapper? = null;
    var lastShotTime:Long = 0;
    var lastAbilityUsed:Long = 0;


    var reloadTask:BukkitRunnable? = null

    fun getItemStack(): ItemStack {
        return player.player.inventory.itemInMainHand;
    }

    //TODO change to just do time comparison
    fun isActiveChipOnCooldown():Boolean{


        return  !canActiveChipBeUsed(plugin.chipDataHandler.getChipCooldown(getItemStack()))

    }



    //assumed to be in seconds
    fun canActiveChipBeUsed(cooldown: Double):Boolean{
        //if it is not in there it can be used
        val lastUsed = activeChipCooldownTracker[plugin.weaponDataHandler.getUniqueId(getItemStack())]
            ?: return true
        val timeElapsed:Duration = (System.currentTimeMillis()-lastUsed).milliseconds;
        return timeElapsed > cooldown.seconds

    }
    //assumed to be in seconds
    fun canWeaponShoot(cooldown:Double):Boolean{
        val timeElapsed:Duration = (System.currentTimeMillis()-lastShotTime).milliseconds;
        return timeElapsed > cooldown.seconds
    }

    fun hasActiveChipSlot():Boolean{
        return plugin.weaponDataHandler.hasActiveChipSlot(getItemStack())
    }

    fun getActiveChipId():String?{
        return plugin.chipDataHandler.getActiveChipId(getItemStack())
    }

    fun useActiveChip():Boolean{
        return plugin.chipDataHandler.runActiveChip(getActiveChipId() ?: "",player)
    }

    fun activeChipUsed(){
        val itemID = plugin.weaponDataHandler.getUniqueId(getItemStack()) ?: return
        activeChipCooldownTracker[itemID] = System.currentTimeMillis();
    }
    fun weaponShot(){
        lastShotTime = System.currentTimeMillis();
    }
    //checks if it is on full auto
    //TODO check if it is currently burst firing
    fun isCurrentlyFiring():Boolean{

        if(fullAutoTask == null) return false;


        if(fullAutoTask!!.isCancelled()){
            fullAutoTask = null;
            return false
        }
        return true;
    }

    fun isCharging():Boolean{
        if(chargingTask == null) return false;


        if(chargingTask!!.isCancelled()){
            chargingTask = null;
            return false
        }
        return true;
    }

    fun reduceWeaponAmmo(){
        getItemStack().editMeta {
            it.persistentDataContainer.set(NamespacedKey(plugin,"ammoLeft"),
                PersistentDataType.INTEGER,
                it.persistentDataContainer.get(
                    NamespacedKey(plugin,"ammoLeft"),
                    PersistentDataType.INTEGER
                )!!-1)
        }
        player.playerUI.updateBoard();
    }

    fun isPrimaryFireOnCooldown():Boolean{
        val cooldown = getFireCooldown()
        //apply modifiers
        return canWeaponShoot(cooldown)
    }


    //wrappers to the weapon data handler
    fun getReloadTime():Double{
        return plugin.weaponDataHandler.getReloadTime(getItemStack());
    }

    fun getMaxAmmo():Int{
        return plugin.weaponDataHandler.getTotalAmmo(getItemStack());
    }

    fun getChargeTime():Double{
        return  plugin.weaponDataHandler.getChargeTime(getItemStack())
    }

    fun getFireCooldown():Double{
        return  plugin.weaponDataHandler.getFireCooldown(getItemStack());
    }
    fun getFireRate():Double{
        return  plugin.weaponDataHandler.getFireRate(getItemStack())
    }
    //TODO add validation that it is infact a weapon with ammo

    //TODO add some validation
    fun getAmmoLeft():Int{
        return getItemStack().itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"ammoLeft"),
            PersistentDataType.INTEGER) ?: 0

    }

    fun setWeaponAmmo(toSet:Int){

        val item = getItemStack()

        item.editMeta {
            it.persistentDataContainer.set(NamespacedKey(plugin,"ammoLeft"), PersistentDataType.INTEGER,toSet)
        }
        player.playerUI.updateBoard();
    }


    fun getWeaponYamlData():ConfigurationSection?{
        val id = getItemStack().itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return null;
        return YamlConfiguration.loadConfiguration(File(plugin.dataFolder,"/RangedWeaponData.yml")).getConfigurationSection(id) ?: return null
    }



    fun isReloading():Boolean{
        if(reloadTask == null) return false

        if(reloadTask!!.isCancelled){
            reloadTask = null
            return false
        }
        return true
    }

    /**
     * clear all active tasks
     * if zoomed in reset
     */
    fun reset(){
        if(fullAutoTask!=null) fullAutoTask!!.cancel()
        fullAutoTask = null

        if(reloadTask!=null) reloadTask!!.cancel()
        reloadTask = null

        if(zoomedIn){
            ScopeHandler.zoomOut(player)
        }

        lastShotTime = 0;

    }



}