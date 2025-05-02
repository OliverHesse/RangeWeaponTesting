package me.Lucent.Handlers

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.entity.Player

import kotlin.collections.HashMap

class PlayerWrapperHandler(val plugin: RangedWeaponsTest) {
    private val playerWrappers:HashMap<Player,PlayerWrapper> = HashMap();

    fun getPlayerWrapper( player: Player):PlayerWrapper{
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