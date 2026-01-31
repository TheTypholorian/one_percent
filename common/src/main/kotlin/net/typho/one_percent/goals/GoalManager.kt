package net.typho.one_percent.goals

import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component
import kotlin.random.Random

interface GoalManager<G : Goal> {
    fun pickGoal(registries: RegistryAccess, random: Random, alreadyPicked: Collection<Goal> = emptySet()): G

    fun name(): Component

    companion object {
        @JvmField
        val ALL_MANAGERS: MutableList<GoalManager<*>> = mutableListOf(ItemGoalManager)
    }
}