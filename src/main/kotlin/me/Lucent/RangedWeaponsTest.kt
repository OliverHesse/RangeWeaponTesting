package me.Lucent

import org.bukkit.plugin.java.JavaPlugin

class RangedWeaponsTest : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic

        server.pluginManager.registerEvents(WeaponController(this),this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
