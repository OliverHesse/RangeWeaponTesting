package me.Lucent.Mechanics.StatProfiles

import kotlinx.serialization.Serializable
import me.Lucent.Enums.DamageType


//can be stored in PersistentContainers by serializing and deserializing
@Serializable
data class WeaponStatProfile(
    val damageTypeMap:Map<DamageType,Double>,
    val statusChance:Double,
    val criticalChance:Double,
    val criticalDamage:Double,
)