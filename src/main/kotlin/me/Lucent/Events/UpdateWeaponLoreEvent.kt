package me.Lucent.Events

import me.Lucent.Mechanics.StatProfiles.WeaponStatModifierProfile
import me.Lucent.Wrappers.PlayerWrapper

class UpdateWeaponLoreEvent(val playerWrapper: PlayerWrapper, val statModifierProfile: WeaponStatModifierProfile) {
}