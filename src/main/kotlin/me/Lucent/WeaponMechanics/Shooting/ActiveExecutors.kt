package me.Lucent.WeaponMechanics.Shooting

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import me.Lucent.Handlers.WeaponHandlers.ScopeHandler
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile


//Used by both primary fire and ability slots
//Primary exclusive functions are denoted by Primary
//Primary abilities may have vararg
object ActiveExecutors {
    val executorNameToFunction = listOf(
        ::primaryCreateFullAutoProjectileTask,
        ::scopeX4,
        ::SingleShotExplosiveProjectile
        ).associateBy { it.name }

    fun primaryCreateFullAutoProjectileTask(player:PlayerWrapper){

    }
    //2 args radius and damageRatio expected
    fun SingleShotExplosiveProjectile(player: PlayerWrapper,vararg args: Any){
        //verification
        if(args.size != 2) return
        if(args[0] is Double && args[1] is Double){
            //TODO add last shot Time here
            val snowball: Projectile = player.player.world.spawnEntity(player.player.eyeLocation,
                EntityType.SNOWBALL) as Projectile
            snowball.velocity = player.player.location.direction.multiply(1.5);
            snowball.shooter = player.player;

        } else return;
    }
    //TODO fix bug where there is a delay
    fun scopeX4(player: PlayerWrapper){
        if(player.activeItemData.zoomedIn) ScopeHandler.zoomOut(player);
        else ScopeHandler.zoomIn(player,4f)
    }


}