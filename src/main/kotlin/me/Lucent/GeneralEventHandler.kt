package me.Lucent

import me.Lucent.Enums.DamageType
import me.Lucent.Events.CustomPlayerDamageEntityEvent
import me.Lucent.Events.PlayerAttackEntityEvent
import me.Lucent.Handlers.PlayerWrapperHandler
import me.Lucent.Mechanics.StatProfiles.PlayerDamageEntityEventModifiers
import net.kyori.option.OptionSchema.Mutable
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.sqrt


class GeneralEventHandler(val plugin: RangedWeaponsTest):Listener {


    //mainly used to do all the stuff needed before the player damages the enemy. then calls the player damaged enemy event
    @EventHandler
    fun onCustomPlayerAttackEntity(e:PlayerAttackEntityEvent){

        plugin.logger.info("player ${e.playerWrapper.player.name} attacked entity ${e.target.name}")
        val eventModifiers = PlayerDamageEntityEventModifiers()

        //TODO add weapon modifiers

        val damageEvent = CustomPlayerDamageEntityEvent(e.playerWrapper,e.target, eventModifiers)
        damageEvent.callEvent()
        var weaponStats = e.statProfile
        if(e.item != null) weaponStats = plugin.weaponDataHandler.getStatProfile(e.item!!)!!
        val modifiers = damageEvent.damageModifiers;

        //val criticalDamage = (weaponStats.criticalDamage+modifiers.baseCriticalDamageBonus)*(1+modifiers.criticalDamageMultiplier)+modifiers.finalCriticalDamageBonus
        for(damageType in weaponStats.damageTypeMap.keys){
            val criticalChance =
                (weaponStats.criticalChance +( modifiers.baseCriticalChanceBonus[damageType] ?: 0.0) + (modifiers.baseCriticalChanceBonus[DamageType.Default] ?: 0.0))*
                        (1+( modifiers.criticalChanceMultiplier[damageType] ?: 0.0) + (modifiers.criticalChanceMultiplier[DamageType.Default] ?: 0.0))+
                                ( modifiers.finalCriticalChanceBonus[damageType] ?: 0.0) + (modifiers.finalCriticalChanceBonus[DamageType.Default] ?: 0.0)
            val simpleCritChance = criticalChance- floor(criticalChance)
            //for example at 150% critical bonus has a guarantee x1 crit multiplier and a chance at x2
            var didCrit = 0
            var critBonus = floor(criticalChance).toInt()
            if(ThreadLocalRandom.current().nextDouble(0.0,1.0) <= simpleCritChance) {
                didCrit = 1
                critBonus = ceil(criticalChance).toInt()
            }
            val criticalDamage =
                (weaponStats.criticalDamage +( modifiers.baseCriticalDamageBonus[damageType] ?: 0.0) + (modifiers.baseCriticalDamageBonus[DamageType.Default] ?: 0.0))*
                        (1+( modifiers.criticalDamageMultiplier[damageType] ?: 0.0) + (modifiers.criticalDamageMultiplier[DamageType.Default] ?: 0.0))+
                        ( modifiers.finalCriticalDamageBonus[damageType] ?: 0.0) + (modifiers.finalCriticalDamageBonus[DamageType.Default] ?: 0.0)
            val damageMultiplier =(modifiers.damageMultipliers[damageType] ?: 0.0) +(modifiers.damageMultipliers[DamageType.Default] ?: 0.0) + didCrit*(modifiers.damageMultipliers[DamageType.Critical] ?: 0.0)
            val damageBonus = (modifiers.baseDamageBonus[damageType] ?: 0.0) +(modifiers.baseDamageBonus[DamageType.Default] ?: 0.0) + didCrit*(modifiers.baseDamageBonus[DamageType.Critical] ?: 0.0)
            val finalDamageBonus = (modifiers.finalDamageBonus[damageType] ?: 0.0) +(modifiers.finalDamageBonus[DamageType.Default] ?: 0.0) + didCrit*(modifiers.finalDamageBonus[DamageType.Critical] ?: 0.0)
            var finalDamageMultiplier = 0.0
            var list :MutableList<Double> = ((modifiers.finalDamageMultipliers[damageType] ?: emptyList())+(modifiers.finalDamageMultipliers[DamageType.Default] ?: emptyList())).toMutableList()
            if(didCrit == 1) list += (modifiers.finalDamageMultipliers[DamageType.Critical] ?: emptyList())
            for(multiplier in list){
                finalDamageMultiplier += multiplier
            }
            val damageVulnerability = (modifiers.damageVulnerabilityMultipliers[damageType] ?: 0.0) +(modifiers.damageVulnerabilityMultipliers[DamageType.Default] ?: 0.0) + didCrit*(modifiers.damageVulnerabilityMultipliers[DamageType.Critical] ?: 0.0)



            val damageResistance = (modifiers.damageResistanceMultipliers[damageType] ?: 0.0) +(modifiers.damageResistanceMultipliers[DamageType.Default] ?: 0.0) + didCrit*(modifiers.damageResistanceMultipliers[DamageType.Critical] ?: 0.0)

            val baseArmour = plugin.entityWrapperHandler.getEntityWrapper(e.target).armour
            val defence = (baseArmour+modifiers.baseArmourBonus)*(1+modifiers.armourMultipliers)+modifiers.finalArmourBonus
            val defenceMulti = 1-0.9* sqrt(defence/9000)

            val finalDamage = (weaponStats.damageTypeMap[damageType]!!+damageBonus)*(1+damageMultiplier)*(1+damageVulnerability)*(1+didCrit*critBonus*criticalDamage)*(1-damageResistance)*(1-modifiers.damageReductionMultipliers)*(1+finalDamageMultiplier)+finalDamageBonus-modifiers.damageCut
            plugin.logger.info("base damage ${weaponStats.damageTypeMap[damageType]} $damageType")
            plugin.logger.info("was critical hit = ${didCrit == 1}")
            plugin.logger.info("dealt $finalDamage ${damageType.toString()} damage")

        }

    }

    @EventHandler
    fun onProjectileHit(e:ProjectileHitEvent){
        if(e.entity.shooter !is Player) return

        if(e.hitEntity == null) return

        e.isCancelled = true
        val item =   plugin.projectileHandler.getItemStack(e.entity) ?: return
        val attackEvent = PlayerAttackEntityEvent.create(plugin,
            plugin.playerWrapperHandler.getPlayerWrapper(e.entity.shooter as Player),item,e.hitEntity!!)
        attackEvent.callEvent()
    }
}