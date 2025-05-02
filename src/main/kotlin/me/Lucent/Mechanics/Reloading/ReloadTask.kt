package me.Lucent.Mechanics.Reloading

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.floor

class ReloadTask(private val plugin: RangedWeaponsTest, private val player: PlayerWrapper): BukkitRunnable() {
    private val EMPTY_SQUARE_CHAR = "□"
    private val FILLED_SQUARE_CHAR = "■"

    private val RELOAD_BAR_BOX_NUMBER = 10;

    private var finalReloadTimeTicks:Int = 0

    private var ticksElapsed = 0;
    private var tickPerBoxChar:Double = 0.0;
    private var totalBoxesElapsed = 0

    private var maxAmmo:Int = 0

    init {
            finalReloadTimeTicks =  floor(player.activeItemData.getReloadTime()*20).toInt()
            maxAmmo = player.activeItemData.getMaxAmmo()


            tickPerBoxChar = (finalReloadTimeTicks/RELOAD_BAR_BOX_NUMBER).toDouble();


    }

    //assume it runs every tick
    override fun run() {


        if(ticksElapsed >= finalReloadTimeTicks){
            //set ammo
            player.activeItemData.setWeaponAmmo(player.activeItemData.getMaxAmmo());
            plugin.logger.info("FINISHED RELOADING")
            val loadingBarString = "["+FILLED_SQUARE_CHAR.repeat(RELOAD_BAR_BOX_NUMBER)+"]"
            player.player.sendActionBar(Component.text(loadingBarString).color(TextColor.color(249, 255, 74)))
            this.cancel()
            return

        }


        if(ticksElapsed>tickPerBoxChar*(totalBoxesElapsed+1)) totalBoxesElapsed += 1

        val loadingBarString = "["+FILLED_SQUARE_CHAR.repeat(totalBoxesElapsed)+EMPTY_SQUARE_CHAR.repeat(RELOAD_BAR_BOX_NUMBER-totalBoxesElapsed)+"]"
        player.player.sendActionBar(Component.text(loadingBarString).color(TextColor.color(249, 255, 74)))
        ticksElapsed += 1

    }


}