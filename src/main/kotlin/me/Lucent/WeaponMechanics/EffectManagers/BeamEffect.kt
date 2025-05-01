package me.Lucent.WeaponMechanics.EffectManagers

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.Color
import org.bukkit.Particle


//TODO Improve and restudy 3d vector rotations kinda kiffed rn

class BeamEffect(val plugin: RangedWeaponsTest,val playerWrapper: PlayerWrapper,val beamRadius:Double,val maxLength:Double,val beamColor:Color,val canPenetrate:Boolean) {
    //hacky solution,
    //generate perpendicular vector
    //gen random magnitude and rotation


    val beamDrawIncrements = 0.005;

    fun drawBeam(){
        val dir = playerWrapper.player.eyeLocation.direction.normalize();
        val originLoc = playerWrapper.player.eyeLocation.clone()

        var distanceTraveled = 0.0

        while (distanceTraveled < maxLength ){


            val currentLocation = originLoc.clone().add(dir.clone().multiply(1+distanceTraveled))

            val dust = Particle.DustOptions(beamColor,0.6f)

            currentLocation.world.spawnParticle(Particle.DUST, currentLocation, 0,dust)
            distanceTraveled += beamDrawIncrements;



        }

    }



}