package me.Lucent.Mechanics.WeaponMechanics

import me.Lucent.RangedWeaponsTest
import me.Lucent.Mechanics.Reloading.ReloadTask
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Projectile
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.floor

class FullAutoFireTask(val plugin:RangedWeaponsTest,val player:PlayerWrapper): BukkitRunnable() {
    var delayedRounds:Double = 0.0;
    var roundsPerTick:Double = 0.0;




    override fun run() {

        if(player.isRightClicking()){
            //TODO make this work with all weapon types
            roundsPerTick = plugin.weaponDataHandler.getFireRate(player.activeItemData.getItemStack())/20.0
            delayedRounds = (delayedRounds+roundsPerTick)- floor(delayedRounds+roundsPerTick)
            val numberOfRounds :Int = (delayedRounds+roundsPerTick).toInt()
            if(numberOfRounds == 0) return;

            for(i in 1..numberOfRounds){
                val snowball:Projectile= player.player.world.spawnEntity(player.player.eyeLocation,EntityType.SNOWBALL) as Projectile
                snowball.velocity = player.player.location.direction.multiply(1.5);
                snowball.shooter = player.player;
                plugin.projectileHandler.addProjectile(player.activeItemData.getItemStack(),snowball)

                //handler ammo
                player.activeItemData.reduceWeaponAmmo();
                if(player.activeItemData.getAmmoLeft() == 0){
                    player.activeItemData.reloadTask = ReloadTask(plugin,player)
                    player.activeItemData.reloadTask!!.runTaskTimer(plugin,0,1);


                    this.cancel()
                }


            }
        }else this.cancel()
    }
}