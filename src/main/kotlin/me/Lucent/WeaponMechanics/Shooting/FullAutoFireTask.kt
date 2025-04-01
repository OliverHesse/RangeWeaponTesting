package me.Lucent.WeaponMechanics.Shooting

import com.github.retrooper.packetevents.util.MathUtil
import me.Lucent.Handlers.WeaponHandlers.ShootHandler
import me.Lucent.RangedWeaponsTest
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

    init {
        plugin.logger.info("STARING FULL AUTO")
        var weapon = player.activeItemData.getItemStack();
        var container = weapon.itemMeta.persistentDataContainer;

        if(container.has(NamespacedKey(plugin,"fireRate"), PersistentDataType.DOUBLE)){
            roundsPerTick = container.get(NamespacedKey(plugin,"fireRate"), PersistentDataType.DOUBLE)!!/20.0

        }
    }



    override fun run() {
        plugin.logger.info("running tick")
        plugin.logger.info(ShootHandler.continueFullAuto(player).toString())
        if(ShootHandler.continueFullAuto(player)){
            //TODO make this work with all weapon types

            delayedRounds = (delayedRounds+roundsPerTick)- floor(delayedRounds+roundsPerTick)
            var numberOfRounds :Int = (delayedRounds+roundsPerTick).toInt()
            if(numberOfRounds == 0) return;
            plugin.logger.info("number of rounds = $numberOfRounds")
            plugin.logger.info("snowball locatoin ${player.player.eyeLocation}")
            for(i in 1..numberOfRounds){
                val snowball:Projectile= player.player.world.spawnEntity(player.player.eyeLocation,EntityType.SNOWBALL) as Projectile
                snowball.velocity = player.player.location.direction.multiply(1.5);
                snowball.shooter = player.player;
            }
        }else this.cancel()
    }
}