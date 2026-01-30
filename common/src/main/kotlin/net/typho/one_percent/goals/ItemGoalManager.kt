package net.typho.one_percent.goals

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.SpawnEggItem
import net.typho.one_percent.OnePercent.UNOBTAINABLE
import kotlin.random.Random

object ItemGoalManager : GoalManager<ItemGoal> {
    override fun pickGoal(registries: RegistryAccess, random: Random): ItemGoal {
        val items = registries.registryOrThrow(Registries.ITEM)
        val goals = ArrayList(
            items.stream()
                .filter { item ->
                    !items.getTag(UNOBTAINABLE)
                        .orElseThrow()
                        .any { holder -> holder.value() == item }
                }
                .filter { item -> item !is SpawnEggItem }
                .filter { item -> !MusicDiscGoal.test(item.defaultInstance) }
                .filter { item -> !PotterySherdGoal.test(item.defaultInstance) }
                .map<ItemGoal>(::SingleItemGoal)
                .toList()
        )

        goals.add(MusicDiscGoal)
        goals.add(PotterySherdGoal)

        return goals.random(random)
    }

    override fun name(): Component = Component.translatable("one_percent.item_goal")
}