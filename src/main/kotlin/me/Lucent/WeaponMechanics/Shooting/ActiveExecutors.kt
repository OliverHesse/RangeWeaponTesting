package me.Lucent.WeaponMechanics.Shooting

import me.Lucent.Events.PlayerAttackEntityEvent
import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.EffectManagers.BeamEffect
import me.Lucent.WeaponMechanics.EffectManagers.HitScanEffect
import me.Lucent.Wrappers.PlayerWrapper
import me.Lucent.Wrappers.RunnableWrappers.GeneralWrapper
import org.bukkit.Color
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Projectile
import org.bukkit.util.Vector

//must return true false to ensure it was successful

//Used by both primary fire and ability slots
//Primary exclusive functions are denoted by Primary.
//Primary abilities may have vararg
object ActiveExecutors {
    val executorNameToFunction = listOf(
        ::primaryCreateFullAutoProjectileTask,
        ::scope,
        ::primarySingleShotExplosiveProjectile,
        ::singleShotExplosiveProjectile,
        ::singleShotBeam,
        ::singleShotHitScan,
        ::fullChargeWeapon
        ).associateBy { it.name }
    //TODO Modify to work with new config system
    fun fullChargeWeapon(plugin: RangedWeaponsTest,player: PlayerWrapper,vararg args:Any):Boolean{
        if(args.isEmpty()) return false
        if(args[0] !is String) return  false

        val task = FullChargeWeaponTask(plugin,player,args[0] as String,*args.drop(1).toTypedArray())
        player.activeItemData.chargingTask = GeneralWrapper(plugin,player,task)
        player.activeItemData.chargingTask!!.task.runTaskTimer(plugin,0,1);
        return true
    }


    fun primaryCreateFullAutoProjectileTask(plugin:RangedWeaponsTest,player:PlayerWrapper, vararg args:Any):Boolean{

        player.activeItemData.fullAutoTask = GeneralWrapper(plugin,player,FullAutoFireTask(plugin,player))
        player.activeItemData.fullAutoTask!!.task.runTaskTimer(plugin,0,1)

        return true
    }

    fun singleShotProjectile(){}

    //calls SingleShotExplosiveProjectile but has some extra verification
    //2 args radius and damageRatio expected
    fun primarySingleShotExplosiveProjectile(plugin:RangedWeaponsTest,player: PlayerWrapper,vararg args: Any):Boolean{
        //no verification cus it is done by actual function
        if(!(player.activeItemData.isPrimaryFireOnCooldown())) return false
        return singleShotExplosiveProjectile(plugin, player,*args)
    }
    //TODO modfiy to work properly as explosive
    fun singleShotExplosiveProjectile(plugin:RangedWeaponsTest, player: PlayerWrapper, vararg args:Any):Boolean{

        //verification
        if(args.size != 2) return false
        if(!(args[0] is Double && args[1] is Double)) return  false



        val snowball: Projectile = player.player.world.spawnEntity(player.player.eyeLocation,EntityType.SNOWBALL) as Projectile
        snowball.velocity = player.player.location.direction.multiply(2);
        snowball.shooter = player.player;


        //TODO make a bit safer...
        player.activeItemData.reduceWeaponAmmo()


        return true;
    }
    //TODO bug where 2 ammo is consumed
    fun singleShotHitScan(plugin:RangedWeaponsTest,player: PlayerWrapper, vararg args:Any):Boolean{
        if(!(player.activeItemData.isPrimaryFireOnCooldown())) return false
        if(args.size != 1) return false
        if(args[0] !is Double) return false
        val rayResult = WeaponRayTrace(plugin,player,0.1,args[0] as Double,false).shootTrace()
        player.activeItemData.reduceWeaponAmmo()
        if(rayResult[0] == null) return false
        val traceHit = HitScanEffect(plugin,player, Color.RED,rayResult[0]!!.hitPosition);
        traceHit.drawEffect()
        if(rayResult[0]!!.hitBlock != null) return false;
        val attackEvent = PlayerAttackEntityEvent(plugin,player,player.activeItemData.getItemStack(),rayResult[0]!!.hitEntity!!)
        attackEvent.callEvent()


        return true
    }

    fun singleShotBeam(plugin:RangedWeaponsTest,player: PlayerWrapper, vararg args:Any):Boolean{
        //default to blue for now


        if(!(player.activeItemData.isPrimaryFireOnCooldown())) return false

        if(args.size != 3) return false;


        if(args[0] !is Double || args[1] !is Double || args[2] !is Boolean) return  false


        val beamResults = WeaponRayTrace(plugin,player,args[0] as Double,args[1] as Double,args[2] as Boolean).shootTrace();


        val playerLoc = player.player.eyeLocation.clone().toVector();
        //find the furthest target
        var furthest : Vector = playerLoc.clone()
        var targetHit:Boolean = false;
        for (result in beamResults){
            if(result == null) continue
            targetHit = true
            if(playerLoc.distanceSquared(result.hitPosition) > playerLoc.distanceSquared(furthest)) furthest = result.hitPosition
            //should skip blocks
            if(result.hitEntity == null) continue




            if(result.hitEntity !is LivingEntity) continue

            val attackEvent = PlayerAttackEntityEvent(plugin,player,player.activeItemData.getItemStack(),result.hitEntity!!)
            attackEvent.callEvent()
            if(attackEvent.cancelled) continue

            //damage calculations not done here
        }

        var distance = playerLoc.distance(furthest)
        //it was only null results
        if(!targetHit) distance = args[1] as Double
        val beamEffect = BeamEffect(plugin,player,args[0] as Double,distance, Color.BLUE,args[2] as Boolean);
        beamEffect.drawBeam();

        player.activeItemData.reduceWeaponAmmo()
        return true

    }

    //TODO fix bug where there is a delay
    fun scope(plugin:RangedWeaponsTest,player: PlayerWrapper, vararg args:Any):Boolean{

        if(args.size != 1) return false

        val asDouble:Double = args[0] as Double


        if(player.activeItemData.zoomedIn) ScopeHandler.zoomOut(player);
        else ScopeHandler.zoomIn(player,asDouble.toFloat())

        return true
    }


}