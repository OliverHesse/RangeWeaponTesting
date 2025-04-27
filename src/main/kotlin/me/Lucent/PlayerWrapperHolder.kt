package me.Lucent

import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

import java.util.UUID
import kotlin.collections.HashMap

object PlayerWrapperHolder {
    private val playerWrappers:HashMap<Player,PlayerWrapper> = HashMap();

    fun getPlayerWrapper(plugin:RangedWeaponsTest,player: Player):PlayerWrapper{
        if(playerWrappers.containsKey(player)) return playerWrappers[player]!!

        val wrapper = PlayerWrapper(plugin,player)
        playerWrappers[player] = wrapper
        return wrapper

    }

    //for when a player leaves their data gets added into the queue to be saved
    fun addSaveTask(player: Player){}

    fun removeWrapper(player: Player){
        if(!playerWrappers.containsKey(player)) return;

        addSaveTask(player)
        playerWrappers[player]!!.safeDeleteWrapper()
        playerWrappers.remove(player);
    }
}