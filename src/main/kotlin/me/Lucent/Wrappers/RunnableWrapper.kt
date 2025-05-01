package me.Lucent.Wrappers

import org.bukkit.scheduler.BukkitRunnable

abstract class RunnableWrapper {
    abstract var task:BukkitRunnable

    abstract fun cancel()

    fun isCancelled():Boolean{return task.isCancelled}
}