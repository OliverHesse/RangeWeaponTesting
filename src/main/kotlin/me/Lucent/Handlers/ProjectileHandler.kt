package me.Lucent.Handlers

import me.Lucent.RangedWeaponsTest
import org.bukkit.entity.Item
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

//used to track projectiles and what item stack is the source
class ProjectileHandler(val plugin: RangedWeaponsTest) {

    var projectileToItem = mutableMapOf<Projectile,ItemStack>()




    fun addProjectile(item: ItemStack,projectile: Projectile){
        projectileToItem[projectile] = item
    }

    fun getItemStack(projectile: Projectile):ItemStack?{
        return projectileToItem[projectile]
    }

    fun removeProjectile(projectile: Projectile){
        projectileToItem.remove(projectile)
    }




}