package me.Lucent.WeaponMechanics.ParticleEffectManagers

import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs


//TODO Improve and restudy 3d vector rotations kinda kiffed rn

class BeamEffect(val plugin: Plugin,val playerWrapper: PlayerWrapper,val beamRadius:Double,val maxLength:Double,val beamColor:Color,val canPenetrate:Boolean) {
    //hacky solution,
    //generate perpendicular vector
    //gen random magnitude and rotation

    //every 0.1 blocks draw new particles
    val beamDrawIncrements = 0.005;
    val particlesPerInc = 100;


    //now had dafuc do i rotate this
    fun generateRelativeParticleLocation(axis:Vector):Vector{

        val randMag = ThreadLocalRandom.current().nextDouble(-beamRadius,beamRadius)

        val randAngle = ThreadLocalRandom.current().nextDouble(0.0,360.0);

        val perp = getPerpendicularVector(axis)
        perp.multiply(randMag)
        perp.rotateAroundAxis(axis,randAngle)
        //so it is not super flat

        return perp
    }
    fun getPerpendicularVector(v: Vector): Vector {

        val temp = if (abs(v.x) < abs(v.z)) Vector(1.0, 0.0, 0.0) else Vector(0.0, 0.0, 1.0)


        return v.clone().crossProduct(temp).normalize()
    }
    //all particles are represented as a vector where z axis should be rotated to match the direction
    fun drawBeam(){
        val dir = playerWrapper.player.location.direction.normalize();
        val originLoc = playerWrapper.player.eyeLocation

        var distanceTraveled = 0.0

        while (distanceTraveled < maxLength ){
            val current_Location = originLoc.clone().add(dir.multiply(1+distanceTraveled))
            for(i in  0..particlesPerInc){
                val loc = generateRelativeParticleLocation(dir);
                val actual_loc = current_Location.add(loc)

                val dust = Particle.DustOptions(beamColor,0.6f)

                actual_loc.world.spawnParticle(Particle.DUST, actual_loc, 0,dust)
            }


            distanceTraveled += beamDrawIncrements;
        }
    }



}