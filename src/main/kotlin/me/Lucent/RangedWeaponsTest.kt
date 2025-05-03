package me.Lucent

import me.Lucent.Handlers.*
import me.Lucent.Mechanics.WeaponMechanics.ActiveExecutors
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RangedWeaponsTest : JavaPlugin() {

    lateinit var weaponDataHandler: WeaponDataHandler
    lateinit var chipDataHandler: ChipDataHandler
    lateinit var projectileHandler: ProjectileHandler
    lateinit var activeExecutors: ActiveExecutors
    lateinit var playerWrapperHandler: PlayerWrapperHandler
    lateinit var entityWrapperHandler: EntityWrapperHandler

    override fun onEnable() {
        // Plugin startup logic
        if(!dataFolder.exists()) dataFolder.mkdir()
        saveResource("RangedWeaponData.yml",true)
        saveResource("ActiveChips.yml",true)
        weaponDataHandler = WeaponDataHandler(this,YamlConfiguration.loadConfiguration(File(dataFolder,"/RangedWeaponData.yml")))
        chipDataHandler = ChipDataHandler(this,YamlConfiguration.loadConfiguration(File(dataFolder,"/ActiveChips.yml")))
        projectileHandler = ProjectileHandler(this)
        activeExecutors = ActiveExecutors(this)
        playerWrapperHandler = PlayerWrapperHandler(this)
        entityWrapperHandler = EntityWrapperHandler(this)
        //used to fix bug
        activeExecutors.executorNameToFunction["testCall"]?.call()
        server.pluginManager.registerEvents(PlayerController(this),this)
        server.pluginManager.registerEvents(GeneralEventHandler(this),this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }






}
