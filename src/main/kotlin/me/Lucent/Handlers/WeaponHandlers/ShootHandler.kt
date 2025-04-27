package me.Lucent.Handlers.WeaponHandlers

import me.Lucent.Wrappers.PlayerWrapper

object ShootHandler {



    //for now only check if user is rightclicking

    fun continueFullAuto(player:PlayerWrapper):Boolean{
        return player.isRightClicking()

    }
}