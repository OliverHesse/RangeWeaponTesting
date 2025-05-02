package me.Lucent.Mechanics.WeaponMechanics

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.floor

class FullChargeWeaponTask(val plugin:RangedWeaponsTest,val player: PlayerWrapper,val shootFunctionName:String,vararg args:Any): BukkitRunnable() {

    private val EMPTY_SQUARE_CHAR = "□"
    private val FILLED_SQUARE_CHAR = "■"
    private val RELOAD_BAR_BOX_NUMBER = 10
    private var totalBoxesElapsed:Int = 0
    private var ticksElapsed:Int = 0
    private var finalChargeTimeTicks = 0
    private var tickPerBoxChar:Double = 0.0
    private var functionArgs:Array<out Any> = emptyArray()

    init {
        //for some reason i cant call args from other methods
        functionArgs = args
        val chargeTime = player.activeItemData.getChargeTime()

        finalChargeTimeTicks = floor(chargeTime*20).toInt()

        tickPerBoxChar = (finalChargeTimeTicks/RELOAD_BAR_BOX_NUMBER).toDouble();


    }

    fun drawProgressBar(numberOfBoxes:Int){
        val loadingBarString = "["+FILLED_SQUARE_CHAR.repeat(numberOfBoxes)+EMPTY_SQUARE_CHAR.repeat(RELOAD_BAR_BOX_NUMBER-numberOfBoxes)+"]"

        player.player.sendActionBar(Component.text(loadingBarString).color(TextColor.color(249, 255, 74)))
    }
    override fun run() {

        if(!player.isRightClicking()){
            this.cancel()
            return
        }

        if(ticksElapsed >= finalChargeTimeTicks){

            drawProgressBar(RELOAD_BAR_BOX_NUMBER)
            plugin.logger.info("finished charging")
            plugin.activeExecutors.executorNameToFunction[shootFunctionName]!!.call(player,functionArgs)
            this.cancel()
            return
        }
        if(ticksElapsed>tickPerBoxChar*(totalBoxesElapsed+1)) totalBoxesElapsed += 1

        this.drawProgressBar(totalBoxesElapsed)
        ticksElapsed += 1

    }
}