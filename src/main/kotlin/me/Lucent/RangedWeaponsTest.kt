package me.Lucent

import me.Lucent.Handlers.WeaponDataHandler
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RangedWeaponsTest : JavaPlugin() {

    lateinit var weaponMaker: WeaponDataHandler

    override fun onEnable() {
        // Plugin startup logic
        if(!dataFolder.exists()) dataFolder.mkdir()
        saveResource("RangedWeaponData.yml",true)
        saveResource("ActiveChips.yml",true)
        weaponMaker = WeaponDataHandler(this,YamlConfiguration.loadConfiguration(File(dataFolder,"/RangedWeaponData.yml")))
        server.pluginManager.registerEvents(PlayerController(this),this)
        server.pluginManager.registerEvents(GeneralEventHandler(this),this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }






}
