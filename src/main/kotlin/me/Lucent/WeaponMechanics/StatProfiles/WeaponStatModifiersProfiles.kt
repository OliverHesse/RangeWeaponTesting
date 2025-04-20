package me.Lucent.WeaponMechanics.StatProfiles

import kotlinx.serialization.Serializable


//some of this will be similar to the DamageEventProfile.

@Serializable
class WeaponStatModifiersProfiles(){
    //global is for bonuses applied to all weapons

    val damageBonusMap = mapOf<String,Double>()
    val flatDamageBonusMap = mapOf<String,Double>()

    // for example fire damage deals 120% of original damage
    //format of damageType -> list of bonuses

    val finalDamageBonuses = mapOf<String,List<Double>>()


    val flatFireRateBonus:Double = 0.0;
    val fireRateBonus: Double = 0.0;
    val flatStatusChanceBonus:Double = 0.0;
    val statusChanceBonus:Double = 0.0

}