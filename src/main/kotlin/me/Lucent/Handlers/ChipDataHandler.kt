package me.Lucent.Handlers

import me.Lucent.RangedWeaponsTest
import me.Lucent.WeaponMechanics.Shooting.ActiveExecutors
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.NamespacedKey
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.io.File

//TODO potentially make as only for active chips
class ChipDataHandler(val plugin: RangedWeaponsTest, val dataFile: YamlConfiguration) {
    fun getActiveChipId(item:ItemStack):String?{
        return item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"activeChip"), PersistentDataType.STRING)
    }

    fun getChipCooldown(item: ItemStack):Double{
        return dataFile.getConfigurationSection(getActiveChipId(item) ?: "")?.getDouble("cooldown") ?: 0.0

    }

    fun runActiveChip(chipId:String,playerWrapper: PlayerWrapper):Boolean{
        val chipData = dataFile.getConfigurationSection(chipId) ?: return false

        val executor = chipData.getString("executor")
        val args = chipData.getList("executorArgs") ?: listOf<Any>()

        return ActiveExecutors.executorNameToFunction[executor]!!.call(plugin,playerWrapper,args.toTypedArray())

    }
}