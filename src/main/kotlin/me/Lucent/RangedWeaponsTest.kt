package me.Lucent

import me.Lucent.Handlers.ChipDataHandler
import me.Lucent.Handlers.WeaponDataHandler
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RangedWeaponsTest : JavaPlugin() {

    lateinit var weaponDataHandler: WeaponDataHandler
    lateinit var chipDataHandler: ChipDataHandler

    override fun onEnable() {
        // Plugin startup logic
        if(!dataFolder.exists()) dataFolder.mkdir()
        saveResource("RangedWeaponData.yml",true)
        saveResource("ActiveChips.yml",true)
        weaponDataHandler = WeaponDataHandler(this,YamlConfiguration.loadConfiguration(File(dataFolder,"/RangedWeaponData.yml")))
        chipDataHandler = ChipDataHandler(this,YamlConfiguration.loadConfiguration(File(dataFolder,"/ActiveChips.yml")))
        server.pluginManager.registerEvents(PlayerController(this),this)
        server.pluginManager.registerEvents(GeneralEventHandler(this),this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }






}
