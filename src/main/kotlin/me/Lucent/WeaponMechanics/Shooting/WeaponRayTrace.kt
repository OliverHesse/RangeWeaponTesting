package me.Lucent.WeaponMechanics.Shooting

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import org.bukkit.FluidCollisionMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.util.RayTraceResult
import org.bukkit.util.Vector

class WeaponRayTrace(val plugin:RangedWeaponsTest, val playerWrapper: PlayerWrapper, val raySize:Double, val rayLength:Double, val canPenetrate:Boolean) {



    //TODO add penetrating where it will look for all entities hit
    fun shootTrace():List<RayTraceResult?>{

        val inital_pos = playerWrapper.player.eyeLocation

        val result = inital_pos.world.rayTrace(inital_pos.add(inital_pos.direction.normalize()),inital_pos.direction,rayLength,FluidCollisionMode.ALWAYS,true,raySize,null);
        //2 things i need



        return listOf(result);


    }

}