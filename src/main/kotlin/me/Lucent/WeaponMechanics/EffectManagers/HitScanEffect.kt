package me.Lucent.WeaponMechanics.EffectManagers

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.util.Vector
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.PI
import kotlin.math.abs

class HitScanEffect(val plugin: RangedWeaponsTest, val playerWrapper: PlayerWrapper, val splatterColor:Color,val splatterLocation:Vector) {

    private val OFFSET:Double = 0.1 // how much the splatter is offset from the hit object
    private val SPLATTER_RANDOM_RANGE = 0.1
    private val PARTICLE_NUMBER:Int = 10

    //TODO can be made more efficient using only vector math
    fun generateRelativeParticleDirection(axis:Vector):Vector{

        val randMag = ThreadLocalRandom.current().nextDouble(0.0,0.2);

        val randAngle = ThreadLocalRandom.current().nextDouble(0.0,360.0);

        val axisOffset = ThreadLocalRandom.current().nextDouble(OFFSET,OFFSET+SPLATTER_RANDOM_RANGE)
        val perp = getPerpendicularVector(axis)
        perp.multiply(randMag)
        perp.rotateAroundAxis(axis,(randAngle*PI)/ 180)
        perp.add(axis.normalize().multiply(axisOffset))
        //so it is not super flat

        return perp
    }
    fun getPerpendicularVector(v: Vector): Vector {

        val temp = if (abs(v.x) < abs(v.z)) Vector(1.0, 0.0, 0.0) else Vector(0.0, 0.0, 1.0)


        return v.clone().crossProduct(temp).normalize()
    }
    //TODO modify a bit
    fun drawEffect(){
        //get the inverse direction
        val dir = playerWrapper.player.eyeLocation.direction.normalize().clone().multiply(-1);
        val originLoc = splatterLocation.clone().toLocation(playerWrapper.player.world)

        for(i in 0..PARTICLE_NUMBER){

            val newLocation = generateRelativeParticleDirection(dir.clone()).toLocation(originLoc.world).add(originLoc);
            val dust = Particle.DustOptions(splatterColor,0.3f)

            newLocation.world.spawnParticle(Particle.DUST, newLocation, 0,dust)

        }
    }
}