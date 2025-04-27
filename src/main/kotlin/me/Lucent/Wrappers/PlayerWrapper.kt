package me.Lucent.Wrappers

import me.Lucent.RangedWeaponsTest
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class PlayerWrapper(val plugin:RangedWeaponsTest,val player: Player) {
    //TODO modify to actually work
    var maxHealth:Double = 100.0;
    var health:Double = 100.0;
    fun safeDeleteWrapper(){}


    val activeItemData:ActiveItemData = ActiveItemData(plugin,this)

    val playerUI = UIWrapper(plugin,this)
    private val FULL_AUTO_GRACE_PERIOD: Duration = 215.milliseconds
    private var lastRightClickTime : Long = 0;
    //data on active item in hand. even if there is nothing

    fun isItemEquip() : Boolean{
        return player.inventory.itemInMainHand.type != Material.AIR
    }

    fun isRightClicking():Boolean{
        if(player.isBlocking) return true;

        val duration :Duration = (System.currentTimeMillis()-lastRightClickTime).milliseconds
        plugin.logger.info(duration.toString())
        plugin.logger.info((duration < FULL_AUTO_GRACE_PERIOD).toString())
        return duration < FULL_AUTO_GRACE_PERIOD;
    }


    fun rightClicked(){
        lastRightClickTime = System.currentTimeMillis()
    }


}