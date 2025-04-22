package me.Lucent.WeaponMechanics.Shooting

import com.github.retrooper.packetevents.util.MathUtil
import kotlinx.serialization.json.Json
import me.Lucent.Handlers.WeaponHandlers.ShootHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.StatProfiles.WeaponStatModifiersProfiles
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.entity.EntityType
import org.bukkit.entity.Projectile
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask
import java.util.function.Consumer
import kotlin.math.floor

class FullAutoFireTask(val plugin:RangedWeaponsTest,val player:PlayerWrapper): BukkitRunnable() {
    var delayedRounds:Double = 0.0;
    var roundsPerTick:Double = 0.0;
    var canModify:Boolean = false;
    init {
        //TODO update

        plugin.logger.info("STARING FULL AUTO")
        val weaponData = player.activeItemData.getWeaponYamlData();
        val fireRate = weaponData?.getConfigurationSection("WeaponStats")?.getDouble("fireRate")!!
        val canBeModified = weaponData.getConfigurationSection("WeaponStats")?.getBoolean("fireRateCanBeModified")!!


        roundsPerTick = fireRate/20.0
        canModify = canBeModified;

    }
    fun calculateRoundsThisTick():Double{
        if(!canModify) return roundsPerTick
        val container =  player.activeItemData.getItemStack().itemMeta.persistentDataContainer;

        val statModifierProfilesEncoded = container.get(NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING)

        val statModifierProfiles = Json.decodeFromString<WeaponStatModifiersProfiles>(statModifierProfilesEncoded!!)

        //TODO apply fire rate buffs
        return 0.0
    }


    override fun run() {

        if(ShootHandler.continueFullAuto(player)){
            //TODO make this work with all weapon types
            roundsPerTick = calculateRoundsThisTick();
            delayedRounds = (delayedRounds+roundsPerTick)- floor(delayedRounds+roundsPerTick)
            val numberOfRounds :Int = (delayedRounds+roundsPerTick).toInt()
            if(numberOfRounds == 0) return;
            plugin.logger.info("number of rounds = $numberOfRounds")
            plugin.logger.info("snowball location ${player.player.eyeLocation}")
            for(i in 1..numberOfRounds){
                val snowball:Projectile= player.player.world.spawnEntity(player.player.eyeLocation,EntityType.SNOWBALL) as Projectile
                snowball.velocity = player.player.location.direction.multiply(1.5);
                snowball.shooter = player.player;
            }
        }else this.cancel()
    }
}