package me.Lucent.WeaponMechanics.Reloading

import kotlinx.serialization.json.Json
import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.StatProfiles.WeaponStatModifiersProfiles
import me.Lucent.Wrappers.PlayerWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.floor

class ReloadTask(private val plugin: RangedWeaponsTest, private val player: PlayerWrapper): BukkitRunnable() {

    private val RELOAD_CHAR = "█";
    private val RELOAD_BAR_BOX_NUMBER = 10;

    private var finalReloadTimeTicks:Int = 0

    private var ticksElapsed = 0;
    private var tickPerBoxChar:Double = 0.0;
    private var totalBoxesElapsed = 0

    private var maxAmmo:Int = 0

    init {
        val weaponData = player.activeItemData.getWeaponYamlData()
        val baseReloadTime = weaponData!!.getConfigurationSection("WeaponStats")!!.getDouble("reloadTime")
        val container =  player.activeItemData.getItemStack().itemMeta.persistentDataContainer;

        val statModifierProfilesEncoded = container.get(NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING)

        val statModifierProfiles = Json.decodeFromString<WeaponStatModifiersProfiles>(statModifierProfilesEncoded!!)
        val finalTimeSeconds = (baseReloadTime)/(1+statModifierProfiles.reloadTimeModifier)
        finalReloadTimeTicks = floor(finalTimeSeconds*20).toInt()

        tickPerBoxChar = (finalReloadTimeTicks/RELOAD_BAR_BOX_NUMBER).toDouble();


        val baseMaxAmmo = weaponData.getConfigurationSection("WeaponStats")!!.getInt("maxAmmo")
        val canModify = weaponData.getConfigurationSection("WeaponStats")!!.getBoolean("maxAmmoCanBeModified")

        if(canModify) maxAmmo = floor(baseMaxAmmo*(1+statModifierProfiles.totalAmmoModifier)).toInt()
        else maxAmmo = baseMaxAmmo
    }

    //assume it runs every tick
    override fun run() {


        if(ticksElapsed >= finalReloadTimeTicks){
            //set ammo
            val item = player.activeItemData.getItemStack()

            item.editMeta {
                it.persistentDataContainer.set(NamespacedKey(plugin,"ammoLeft"), PersistentDataType.INTEGER,maxAmmo)
            }
            val loadingBarString = "["+RELOAD_CHAR.repeat(RELOAD_BAR_BOX_NUMBER)+"]"
            player.player.sendActionBar(Component.text(loadingBarString).color(TextColor.color(249, 255, 74)))
            this.cancel()
            return

        }


        if(ticksElapsed>tickPerBoxChar*(totalBoxesElapsed+1)) totalBoxesElapsed += 1
        plugin.logger.info("this should not be here")
        plugin.logger.info(ticksElapsed.toString());
        plugin.logger.info(finalReloadTimeTicks.toString())
        val loadingBarString = "["+RELOAD_CHAR.repeat(totalBoxesElapsed)+"\u200E".repeat(RELOAD_BAR_BOX_NUMBER-totalBoxesElapsed)+"]"
        player.player.sendActionBar(Component.text(loadingBarString).color(TextColor.color(249, 255, 74)))
        ticksElapsed += 1

    }


}