package me.Lucent.Handlers

import com.google.common.collect.MultimapBuilder
import kotlinx.serialization.json.Json
import me.Lucent.Enums.DamageType
import me.Lucent.Events.UpdateWeaponLoreEvent
import me.Lucent.RangedWeaponsTest
import me.Lucent.Mechanics.StatProfiles.WeaponStatModifierProfile
import me.Lucent.Mechanics.StatProfiles.WeaponStatProfile
import me.Lucent.Wrappers.PlayerWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.text.DecimalFormat
import java.util.UUID
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round


//TODO improve to work with melee weapons
class WeaponDataHandler(val plugin: RangedWeaponsTest, val dataFile:YamlConfiguration){

    fun callPrimaryExecutor(item: ItemStack,wrappedPlayer:PlayerWrapper):Boolean{
        plugin.logger.info("trying to call executor")
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return false

        val executor = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getString("primaryFireExecutor") ?: return false
        plugin.logger.info("got executor")
        val args = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getList("executorArgs") ?: listOf<Any>()
        plugin.logger.info("got args")
        return plugin.activeExecutors.executorNameToFunction[executor]!!.call(wrappedPlayer, args.toTypedArray())
    }

    fun getUniqueId(item: ItemStack):String?{
        return item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"uniqueId"),PersistentDataType.STRING)
    }

    fun hasActiveChipSlot(item:ItemStack):Boolean{
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return false
        return dataFile.getConfigurationSection(itemId)?.getConfigurationSection("activeChip")?.getBoolean("hasSlot") ?: false
    }

    fun generateUUIDString():String{
        return UUID.randomUUID().toString()
    }

    //TODO add some text formating
    fun writeWeaponLore(player:PlayerWrapper,item: ItemStack){
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return
        val itemName = dataFile.getConfigurationSection(itemId)?.getString("itemName") ?: return

        //TODO might need some refactoring or just make an overload that has statModifier as a req
        val statModifiers = getModifierProfile(item) ?: return
        val updateWeaponLoreEvent = UpdateWeaponLoreEvent(player,statModifiers)
        item.editMeta {
            val name = Component.text(itemName).color(TextColor.color(255, 212, 56))
            val weapon = dataFile.getConfigurationSection(itemId)!!
            val weaponStats = weapon.getConfigurationSection("WeaponStats")!!.getConfigurationSection("defaultDamageTypes")!!
            it.itemName(name)
            //TODO improve with local functions
            it.lore(buildList<Component> {

                val f = DecimalFormat("0.##")
                for(damageType in weaponStats.getKeys(false)){
                    val damageComponent = Component.text("$damageType: ").color(TextColor.color(143, 143, 143)).decoration(TextDecoration.ITALIC,false)
                    val damageNumber = Component.text(f.format(getDamageOfType(item,DamageType.valueOf(damageType)))).color(TextColor.color(207, 0, 0)).decoration(TextDecoration.ITALIC,false)
                    add(damageComponent.append(damageNumber))
                }
                val statusText = Component.text("Status Chance: ").color(TextColor.color(143, 143, 143)).decoration(TextDecoration.ITALIC,false)
                val statusVal = Component.text(f.format((getStatusChance(item)*100))+"%").color(TextColor.color(207, 0, 0)).decoration(TextDecoration.ITALIC,false)
                add(statusText.append(statusVal))
                val criticalChanceText = Component.text("Crit Chance: ").color(TextColor.color(143, 143, 143)).decoration(TextDecoration.ITALIC,false)
                val criticalChanceVal = Component.text(f.format((getCriticalChance(item)*100))+"%").color(TextColor.color(207, 0, 0)).decoration(TextDecoration.ITALIC,false)
                add(criticalChanceText.append(criticalChanceVal))
                val criticalDamageText = Component.text("Crit Damage: ").color(TextColor.color(143, 143, 143)).decoration(TextDecoration.ITALIC,false)
                val criticalDamageVal = Component.text(f.format((getCriticalDamage(item)*100))+"%").color(TextColor.color(207, 0, 0)).decoration(TextDecoration.ITALIC,false)
                add(criticalDamageText.append(criticalDamageVal))
                add(Component.text(""))
                for( str in weapon.getStringList("itemLore")){
                    //todo add some text formating
                    add(Component.text(str))
                }
            })
        }


    }


    fun getStatProfile(item:ItemStack):WeaponStatProfile?{
        val encodedString = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"statProfile"), PersistentDataType.STRING) ?: return null
        return Json.decodeFromString<WeaponStatProfile>(encodedString)
    }

    fun getModifierProfile(item:ItemStack):WeaponStatModifierProfile?{
        val encodedString = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING) ?: return null
        return Json.decodeFromString<WeaponStatModifierProfile>(encodedString)

    }

    fun canModify(item:ItemStack,stat:String):Boolean{
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return false
        return dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getConfigurationSection(stat)?.getBoolean("canModify") ?: false

    }


    fun getCriticalChance(item:ItemStack):Double {
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseChance = getStatProfile(item)?.criticalChance ?: return 0.0
        if(!canModify(item,"criticalChance")) return baseChance
        return (baseChance+statModifierProfile.baseCriticalChanceBonus)*(1+statModifierProfile.criticalChanceMultiplier)+statModifierProfile.finalCriticalChanceBonus

    }
    fun getCriticalDamage(item:ItemStack):Double{
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseDamage = getStatProfile(item)?.criticalDamage ?: return 0.0
        if(!canModify(item,"criticalDamage")) return baseDamage
        return (baseDamage+statModifierProfile.baseCriticalDamageBonus)*(1+statModifierProfile.criticalDamageMultiplier)+statModifierProfile.finalCriticalDamageBonus
    }
    fun getStatusChance(item:ItemStack):Double{

        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseStatus = getStatProfile(item)?.statusChance ?: return 0.0
        if(!canModify(item,"statusChance")) return baseStatus

        return (baseStatus+statModifierProfile.baseStatusChanceBonus)*(1+statModifierProfile.statusChanceMultiplier)+statModifierProfile.finalStatusChanceBonus
    }

    fun getFireRate(item:ItemStack):Double{
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseRate = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getConfigurationSection("fireRate")?.getDouble("base") ?: return 0.0
        if(!canModify(item,"fireRate")) return baseRate
        return baseRate*(1+statModifierProfile.fireRateModifier)
    }

    fun getFireCooldown(item:ItemStack):Double{
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseCooldown = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getConfigurationSection("fireCooldown")?.getDouble("base") ?: return 0.0
        if(!canModify(item,"fireCooldown")) return baseCooldown
        return baseCooldown/(1+statModifierProfile.fireCooldownModifier)
    }

    fun getChargeTime(item:ItemStack):Double {
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseTime = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getConfigurationSection("chargeTime")?.getDouble("base") ?: return 0.0
        if(!canModify(item,"chargeTime")) return baseTime
        return baseTime/(1+statModifierProfile.chargeTimeModifier)
    }

    fun getTotalAmmo(item:ItemStack):Int{
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0
        val statModifierProfile = getModifierProfile(item) ?: return 0

        val statSection = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getConfigurationSection("ammo") ?: return 0

        val baseMaxAmmo = statSection.getInt("base")
        if(canModify(item,"ammo")) return  floor(baseMaxAmmo*(1+statModifierProfile.totalAmmoModifier)).toInt()
        return baseMaxAmmo
    }

    //reload time = (base time)/(1+modifier)
    fun getReloadTime(item:ItemStack):Double {
        val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseTime = dataFile.getConfigurationSection(itemId)?.getConfigurationSection("WeaponStats")?.getConfigurationSection("reloadTime")?.getDouble("base") ?: return 0.0

        if(canModify(item,"reloadTime"))return baseTime/(1+statModifierProfile.reloadTimeModifier)

        return baseTime
    }

    fun getDamageOfType(item: ItemStack,damageType: DamageType):Double {
        //val itemId = item.itemMeta.persistentDataContainer.get(NamespacedKey(plugin,"id"), PersistentDataType.STRING) ?: return 0.0
        val statModifierProfile = getModifierProfile(item) ?: return 0.0
        val baseDamage = getStatProfile(item)?.damageTypeMap?.get(damageType) ?: return 0.0

        val damageBonus = (statModifierProfile.baseDamageBonus[damageType] ?: 0.0)+(statModifierProfile.baseDamageBonus[DamageType.Default] ?: 0.0)
        val damageMultiplier = 1+(statModifierProfile.damageMultipliers[damageType] ?: 0.0)+(statModifierProfile.damageMultipliers[DamageType.Default] ?: 0.0)
        val finalDamageBonus = 1+(statModifierProfile.finalDamageBonus[damageType] ?: 0.0)+(statModifierProfile.finalDamageBonus[DamageType.Default] ?: 0.0)
        var finalDamageMultipliers = 1.0
        for(multiplier in (statModifierProfile.finalDamageMultipliers[damageType] ?: emptyList())+(statModifierProfile.finalDamageMultipliers[DamageType.Default] ?: emptyList())){
            finalDamageMultipliers *= (1+multiplier)
        }
        return ((baseDamage+damageBonus)*damageMultiplier+finalDamageBonus)*finalDamageMultipliers

    }


    //TODO to potentially get around rounding issues i store it all as integers? between 0 and 100?
    //TODO easy for formating and then i just /100.0 when the value is needed?
    private fun generateStat(configuration: ConfigurationSection?,decimalPlace:Int) : Double{
        if(configuration == null) return 0.0
        val statValue = configuration.getDouble("base")
        //0.0 is the default value
        if(statValue == 0.0){
            val min = configuration.getDouble("min")
            val max = configuration.getDouble("max")
            //i might want min to be 0.0
            if(max != 0.0){
                return round(ThreadLocalRandom.current().nextDouble(min,max)*10.0.pow(decimalPlace))/10.0.pow(decimalPlace)
            }
        }
        return statValue
    }


    fun generateRangedWeapon(itemId:String):ItemStack?{

        val weaponConfig = dataFile.getConfigurationSection(itemId) ?: return null
        val weaponBase = ItemStack(Material.DIAMOND_SWORD,1);

        val weaponStatModifierProfile = WeaponStatModifierProfile()

        //for now use min damage value

        weaponBase.editMeta {
            it.attributeModifiers = MultimapBuilder.hashKeys().hashSetValues().build();
            it.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            it.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)


            it.persistentDataContainer.set(NamespacedKey(plugin,"id"), PersistentDataType.STRING,itemId)
            it.persistentDataContainer.set(NamespacedKey(plugin,"uniqueId"), PersistentDataType.STRING,generateUUIDString())
            //Create base stats
            val statMap = mutableMapOf<DamageType,Double>()
            val weaponStats = weaponConfig.getConfigurationSection("WeaponStats")!!

            val damageStats = weaponStats.getConfigurationSection("defaultDamageTypes")!!
            plugin.logger.info("damage types on weapon ${damageStats.getValues(false)}")
            //listOf("Physical","Heat","Radiation","Chill","Electric")
            for(damageType in damageStats.getKeys(false)){
                if(damageType == null) continue
                statMap[DamageType.valueOf(damageType)] = generateStat(damageStats.getConfigurationSection(damageType),1)

            }

            val statProfile = WeaponStatProfile(
                statMap,
                generateStat(weaponStats.getConfigurationSection("statusChance"),3),
                generateStat(weaponStats.getConfigurationSection("criticalChance"),3),
                generateStat(weaponStats.getConfigurationSection("criticalDamage"),3))
            plugin.logger.info("${statProfile.statusChance} ${statProfile.criticalChance} ${statProfile.criticalDamage}")
            it.persistentDataContainer.set(NamespacedKey(plugin,"statProfile"), PersistentDataType.STRING, Json.encodeToString(statProfile))
            it.persistentDataContainer.set(
                NamespacedKey(plugin,"statModifierProfile"), PersistentDataType.STRING,
                Json.encodeToString(WeaponStatModifierProfile()))


            val hasActiveSlot = weaponConfig.getConfigurationSection("activeChip")?.getBoolean("hasSlot") ?: false
            it.persistentDataContainer.set(NamespacedKey(plugin,"hasActiveSlot"), PersistentDataType.BOOLEAN,hasActiveSlot)
            if(hasActiveSlot){
                val chip = weaponConfig.getConfigurationSection("activeChip")?.getString("slotted")
                if(chip != null){
                    it.persistentDataContainer.set(NamespacedKey(plugin,"activeChip"), PersistentDataType.STRING,chip)
                }
            }
            it.persistentDataContainer.set(NamespacedKey(plugin,"ammoLeft"), PersistentDataType.INTEGER,weaponStats.getConfigurationSection("ammo")!!.getInt("base"))
        }

        return weaponBase;
    }
}