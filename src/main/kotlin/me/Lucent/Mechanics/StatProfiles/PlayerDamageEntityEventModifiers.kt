package me.Lucent.Mechanics.StatProfiles

import me.Lucent.Enums.DamageType


//the WeaponStatModifierProfile is for stats that apply directly to the activeWeapon
class PlayerDamageEntityEventModifiers{
    //mainly split between target modifiers e.g def an resistances and player modifiers

    //basic player modifiers
    val damageMultipliers = mutableMapOf<DamageType,Double>()
    //flat damage bonus applied at the start
    val baseDamageBonus = mutableMapOf<DamageType,Double>()

    // for example fire damage deals 120% of original damage
    val finalDamageMultipliers = mutableMapOf<DamageType,MutableList<Double>>()

    //only applies if the damage type already exists on the weapon
    val finalDamageBonus = mutableMapOf<DamageType,Double>()
    var baseCriticalChanceBonus:Double = 0.0;
    var criticalChanceMultiplier:Double = 0.0
    var finalCriticalChanceBonus:Double = 0.0


    //the multiplier is different from final damage multipliers. since those are done in a different "multiplication" bucket
    var baseCriticalDamageBonus:Double = 0.0;
    var criticalDamageMultiplier:Double = 0.0
    var finalCriticalDamageBonus:Double = 0.0

    fun addWeaponStatModifierProfile(profile: WeaponStatModifierProfile){
        TODO("Implement")
    }
    fun addPlayerStatModifierProfile(profile: PlayerStatModifierProfile){
        TODO("Implement")
    }
}