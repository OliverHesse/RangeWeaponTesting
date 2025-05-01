package me.Lucent.WeaponMechanics.StatProfiles

import kotlinx.serialization.Serializable


//some of this will be similar to the DamageEventProfile.

@Serializable
class WeaponStatModifierProfile(){
    //global is for bonuses applied to all weapons
    // damageType -> multiplier
    //critical damage is a type
    val damageMultipliers = mutableMapOf<String,Double>()
    //applied before all multipliers
    val baseDamageBonus = mutableMapOf<String,Double>()

    // for example fire damage deals 120% of original damage
    val finalDamageMultipliers = mutableMapOf<String,List<Double>>()
    val finalDamageBonus = mutableMapOf<String,Double>()

    var statusChanceMultiplier:Double = 0.0
    var baseStatusChanceBonus: Double = 0.0
    var finalStatusChanceBonus: Double = 0.0

    var fireRateModifier:Double = 0.0

    var fireCooldownModifier:Double = 0.0
    //reload time = (base time)/(1+modifier)
    var reloadTimeModifier:Double = 0.0;

    //always round down
    var totalAmmoModifier:Double = 0.0;

    var chargeTimeModifier:Double  = 0.0;


    var baseCriticalChanceBonus:Double = 0.0;
    var criticalChanceMultiplier:Double = 0.0
    var finalCriticalChanceBonus:Double = 0.0


    //the multiplier is different from final damage multipliers. since those are done in a different "multiplication" bucket
    var baseCriticalDamageBonus:Double = 0.0;
    var criticalDamageMultiplier:Double = 0.0
    var finalCriticalDamageBonus:Double = 0.0




}