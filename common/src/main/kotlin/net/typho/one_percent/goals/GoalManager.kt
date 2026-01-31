package net.typho.one_percent.goals

import net.minecraft.core.RegistryAccess
import net.minecraft.network.chat.Component

interface GoalManager<G : Goal> {
    fun getAllGoals(registries: RegistryAccess, alreadyPicked: Collection<Goal> = emptySet()): Collection<G>

    fun name(): Component
}