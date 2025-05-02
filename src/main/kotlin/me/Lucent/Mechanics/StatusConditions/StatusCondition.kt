package me.Lucent.Mechanics.StatusConditions

import me.Lucent.Wrappers.RunnableWrappers.GeneralWrapper
import org.bukkit.event.Listener

abstract class StatusCondition: Listener {
    abstract val stackCount:Int
    abstract val maxStackCount:Int

    //since some buffs may want individual stacks to behave differently
    //e.g. individual stacks have individual timers
    abstract fun addStacks(int: Int)
    abstract fun reduceStacks(int: Int)
    //used for cleanup
    abstract fun destroy()
}