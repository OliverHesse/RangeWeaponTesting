package me.Lucent

import kotlinx.serialization.json.Json
import me.Lucent.WeaponMechanics.StatProfiles.WeaponStatModifiersProfiles
import me.Lucent.WeaponMechanics.StatProfiles.WeaponStatProfile
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class RangedWeaponsTest : JavaPlugin() {

    override fun onEnable() {
        // Plugin startup logic
        if(!dataFolder.exists()) dataFolder.mkdir()
        saveResource("RangedWeaponData.yml",true)
        saveResource("ActiveChips.yml",true)
        server.pluginManager.registerEvents(PlayerController(this),this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }



    fun createTestExplosiveWeapon():ItemStack{
        val dataFile = YamlConfiguration.loadConfiguration(File(dataFolder,"/RangedWeaponData.yml"))
        val explosiveWeapon = dataFile.getConfigurationSection("BasicGrenadeLauncher")!!

        //for now use min damage value
        val weaponBase = ItemStack(Material.DIAMOND_SWORD,1);

        weaponBase.editMeta {
            it.persistentDataContainer.set(NamespacedKey(this,"id"), PersistentDataType.STRING,"BasicGrenadeLauncher")
            it.displayName(Component.text(explosiveWeapon.getString("itemName")!!))
            it.lore(buildList<Component> {
                for( str in explosiveWeapon.getStringList("itemLore")){
                    //todo add some text formating
                    add(Component.text(str))
                }
                })
            //Create base stats
            val statMap = mutableMapOf<String,Double>()
            val weaponStats = explosiveWeapon.getConfigurationSection("WeaponStats")
            val damageStats = weaponStats?.getConfigurationSection("defaultDamageTypes")
            for(damageType in listOf("Physical","Heat","Radiation","Chill","Electric")){
                statMap[damageType] = damageStats?.getConfigurationSection(damageType)?.getDouble("min") ?: 0.0
            }
            val statProfile = WeaponStatProfile(statMap,weaponStats?.getDouble("minBaseStatus") ?: 0.0)

            it.persistentDataContainer.set(NamespacedKey(this,"statProfile"), PersistentDataType.STRING, Json.encodeToString(statProfile))
            it.persistentDataContainer.set(NamespacedKey(this,"statModifierProfile"), PersistentDataType.STRING,Json.encodeToString(WeaponStatModifiersProfiles()))
            it.persistentDataContainer.set(NamespacedKey(this,"activeChip"), PersistentDataType.STRING,explosiveWeapon.getString("activeChip")!!)

        }
        return weaponBase;
    }




}
