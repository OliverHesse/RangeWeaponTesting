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
    //change since a buff could be increase crit chance of Chill damage
    var baseCriticalChanceBonus = mutableMapOf<DamageType,Double>()
    var criticalChanceMultiplier =  mutableMapOf<DamageType,Double>()
    var finalCriticalChanceBonus = mutableMapOf<DamageType,Double>()


    //the multiplier is different from final damage multipliers. since those are done in a different "multiplication" bucket
    var baseCriticalDamageBonus = mutableMapOf<DamageType,Double>()
    var criticalDamageMultiplier = mutableMapOf<DamageType,Double>()
    var finalCriticalDamageBonus = mutableMapOf<DamageType,Double>()



    val damageVulnerabilityMultipliers = mutableMapOf<DamageType,Double>()


    //resistance pen is - resistance up is +
    val damageResistanceMultipliers = mutableMapOf<DamageType,Double>()

    //no damageType
    val damageReductionMultipliers = 0.0

    //after all damage is done reduces by this amount and then selects max(1,damageCut)
    val damageCut = 0.0

    var armourMultipliers:Double = 0.0
    var baseArmourBonus:Double = 0.0
    var finalArmourBonus:Double = 0.0

    fun addWeaponStatModifierProfile(profile: WeaponStatModifierProfile){
        TODO("Implement")
    }
    fun addPlayerStatModifierProfile(profile: PlayerStatModifierProfile){
        TODO("Implement")
    }
}