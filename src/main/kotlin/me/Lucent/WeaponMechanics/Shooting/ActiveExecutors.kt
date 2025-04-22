package me.Lucent.WeaponMechanics.Shooting

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile

//must return true false to ensure it was successful

//Used by both primary fire and ability slots
//Primary exclusive functions are denoted by Primary.
//Primary abilities may have vararg
object ActiveExecutors {
    val executorNameToFunction = listOf(
        ::primaryCreateFullAutoProjectileTask,
        ::scope,
        ::primarySingleShotExplosiveProjectile,
        ::singleShotExplosiveProjectile
        ).associateBy { it.name }

    fun primaryCreateFullAutoProjectileTask(plugin:RangedWeaponsTest,player:PlayerWrapper, vararg args:Any):Boolean{

        player.activeItemData.fullAutoTask = FullAutoFireTask(plugin,player)
        player.activeItemData.fullAutoTask!!.runTaskTimer(plugin,0,1)

        return true
    }
    //calls SingleShotExplosiveProjectile but has some extra verification
    //2 args radius and damageRatio expected
    fun primarySingleShotExplosiveProjectile(plugin:RangedWeaponsTest,player: PlayerWrapper,vararg args: Any):Boolean{
        //no verification cus it is done by actual function
        val cooldown = player.activeItemData.getWeaponYamlData()?.getConfigurationSection("WeaponStats")?.getDouble("fireCooldown") ?: return false
        if(!(player.activeItemData.canWeaponShoot(cooldown))) return false

        return singleShotExplosiveProjectile(plugin, player,*args)
    }

    fun singleShotExplosiveProjectile(plugin:RangedWeaponsTest, player: PlayerWrapper, vararg args:Any):Boolean{
        plugin.logger.info(args[0].toString())
        //verification
        if(args.size != 2) return false
        if(!(args[0] is Double && args[1] is Double)) return  false

        val snowball: Projectile = player.player.world.spawnEntity(player.player.eyeLocation,EntityType.SNOWBALL) as Projectile
        snowball.velocity = player.player.location.direction.multiply(1.5);
        snowball.shooter = player.player;

        return true;
    }


    //TODO fix bug where there is a delay
    fun scope(plugin:RangedWeaponsTest,player: PlayerWrapper, vararg args:Any):Boolean{
        plugin.logger.info("scoping in")
        if(args.size != 1) return false
        plugin.logger.info("args correct")
        val asDouble:Double = args[0] as Double
        plugin.logger.info("converted to double")

        if(player.activeItemData.zoomedIn) ScopeHandler.zoomOut(player);
        else ScopeHandler.zoomIn(player,asDouble.toFloat())

        return true
    }


}