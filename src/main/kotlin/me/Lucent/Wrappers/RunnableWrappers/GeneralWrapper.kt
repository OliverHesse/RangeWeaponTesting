package me.Lucent.Wrappers.RunnableWrappers

import me.Lucent.RangedWeaponsTest
import me.Lucent.Wrappers.PlayerWrapper
import me.Lucent.Wrappers.RunnableWrapper
import org.bukkit.scheduler.BukkitRunnable

class GeneralWrapper(private val plugin: RangedWeaponsTest, private val player: PlayerWrapper, override var task:BukkitRunnable): RunnableWrapper() {



    override fun cancel() {
        task.cancel()
    }
}