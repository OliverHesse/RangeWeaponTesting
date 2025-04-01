package me.Lucent.Wrappers

import me.Lucent.RangedWeaponsTest
import org.bukkit.entity.Player
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlayerWrapper(val plugin:RangedWeaponsTest,val player: Player) {

    private val FULL_AUTO_GRACE_PERIOD: Duration = 215.milliseconds
    private var lastRightClickTime : Long = 0;
    //data on active item in hand. even if there is nothing
    val activeItemData:ActiveItemData = ActiveItemData(plugin,this)



    fun isRightClicking():Boolean{
        if(player.isBlocking) return true;

        var duration :Duration = (System.currentTimeMillis()-lastRightClickTime).milliseconds
        plugin.logger.info(duration.toString())
        plugin.logger.info((duration < FULL_AUTO_GRACE_PERIOD).toString())
        return duration < FULL_AUTO_GRACE_PERIOD;
    }


    fun rightClicked(){
        kotlin.system.measureTimeMillis {  }
        lastRightClickTime = System.currentTimeMillis()
    }


}