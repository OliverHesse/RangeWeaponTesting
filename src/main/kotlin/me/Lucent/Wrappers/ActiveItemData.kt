package me.Lucent.Wrappers

import kotlinx.serialization.json.Json
import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.StatProfiles.WeaponStatModifierProfile
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import kotlin.math.floor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ActiveItemData(val plugin:RangedWeaponsTest,val player:PlayerWrapper){


    var fullAutoTask:BukkitRunnable? = null;
    var chargingTask:BukkitRunnable? = null;


    var abilityActive = false;

    //TODO remove and replace with abilityActive
    var zoomedIn = false;
    var abilityCooldownTask:BukkitRunnable? = null;
    var lastShotTime:Long = 0;
    var lastAbilityUsed:Long = 0;


    var reloadTask:BukkitRunnable? = null

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


        if(fullAutoTask!!.isCancelled){
            fullAutoTask = null;
            return false
        }
        return true;
    }

    fun isCharging():Boolean{
        if(chargingTask == null) return false;


        if(chargingTask!!.isCancelled){
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
        val cooldown = getWeaponYamlData()?.getConfigurationSection("WeaponStats")?.getDouble("fireCooldown") ?: return false
        //apply modifiers
        return canWeaponShoot(cooldown)
    }


    //TODO add validation that it infact has a reload time
    //TODO add in reloadTime modifer
    fun getReloadTime():Double{
        val weaponData = getWeaponYamlData();

        val baseReloadTime = weaponData!!.getConfigurationSection("WeaponStats")!!.getDouble("reloadTime")
        val container =  getItemStack().itemMeta.persistentDataContainer;

        val statModifierProfilesEncoded = container.get(NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING)

        val statModifierProfiles = Json.decodeFromString<WeaponStatModifierProfile>(statModifierProfilesEncoded!!)
        return  (baseReloadTime)/(1+statModifierProfiles.reloadTimeModifier)

    }
    //TODO add validation that it is infact a weapon with ammo
    fun getWeaponMaxAmmo():Int{
        val weaponData = player.activeItemData.getWeaponYamlData()

        val container =  player.activeItemData.getItemStack().itemMeta.persistentDataContainer;

        val statModifierProfilesEncoded = container.get(NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING)

        val statModifierProfiles = Json.decodeFromString<WeaponStatModifierProfile>(statModifierProfilesEncoded!!)


        val baseMaxAmmo = weaponData!!.getConfigurationSection("WeaponStats")!!.getInt("maxAmmo")
        val canModify = weaponData.getConfigurationSection("WeaponStats")!!.getBoolean("maxAmmoCanBeModified")

        if(canModify) return  floor(baseMaxAmmo*(1+statModifierProfiles.totalAmmoModifier)).toInt()
        return baseMaxAmmo
    }
    //TODO add some validation
    fun getAmmoLeft():Int{
        return getItemStack().itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"ammoLeft"),
            PersistentDataType.INTEGER)!!

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
        lastAbilityUsed = 0;
    }



}