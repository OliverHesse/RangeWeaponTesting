package me.Lucent.WeaponMechanics.StatProfiles

import kotlinx.serialization.Serializable


//can be stored in PersistentContainers by serializing and deserializing
@Serializable
data class WeaponStatProfile(
    val damageTypeMap:Map<String,Double>,
    val statusChance:Double,
    val criticalChance:Double,
    val criticalDamage:Double,

)