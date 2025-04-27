package me.Lucent.WeaponMechanics.Shooting

import kotlinx.serialization.json.Json
import me.Lucent.Handlers.WeaponHandlers.ShootHandler
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.StatProfiles.WeaponStatModifiersProfiles
import me.Lucent.Wrappers.PlayerWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import javax.swing.text.StyledEditorKit.BoldAction
import kotlin.math.floor
import kotlin.reflect.KCallable

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
        val weaponData = player.activeItemData.getWeaponYamlData()
        val baseReloadTime = weaponData!!.getConfigurationSection("WeaponStats")!!.getDouble("chargeTime")
        val container =  player.activeItemData.getItemStack().itemMeta.persistentDataContainer;

        val statModifierProfilesEncoded = container.get(NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING)

        val statModifierProfiles = Json.decodeFromString<WeaponStatModifiersProfiles>(statModifierProfilesEncoded!!)
        val finalTimeSeconds = (baseReloadTime)/(1+statModifierProfiles.chargeTimeModifier)
        finalChargeTimeTicks = floor(finalTimeSeconds*20).toInt()

        tickPerBoxChar = (finalChargeTimeTicks/RELOAD_BAR_BOX_NUMBER).toDouble();


    }

    fun drawProgressBar(numberOfBoxes:Int){
        val loadingBarString = "["+FILLED_SQUARE_CHAR.repeat(numberOfBoxes)+EMPTY_SQUARE_CHAR.repeat(RELOAD_BAR_BOX_NUMBER-numberOfBoxes)+"]"
        player.player.sendActionBar(Component.text(loadingBarString).color(TextColor.color(249, 255, 74)))
    }
    override fun run() {
        plugin.logger.info("click status: ${player.isRightClicking()}")
        if(!player.isRightClicking()){
            this.cancel()
            return
        }

        if(ticksElapsed >= finalChargeTimeTicks){

            drawProgressBar(RELOAD_BAR_BOX_NUMBER)
            this.cancel()
            ActiveExecutors.executorNameToFunction[shootFunctionName]!!.call(plugin,player,functionArgs)
            return
        }
        if(ticksElapsed>tickPerBoxChar*(totalBoxesElapsed+1)) totalBoxesElapsed += 1

        this.drawProgressBar(totalBoxesElapsed)
        ticksElapsed += 1

    }
}