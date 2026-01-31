package net.typho.one_percent.goals

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.item.TippedArrowItem
import net.typho.one_percent.OnePercent.UNOBTAINABLE
import net.typho.one_percent.mixin.goals.PotionAccessor
import kotlin.random.Random

object ItemGoalManager : GoalManager<ItemGoal> {
    override fun pickGoal(registries: RegistryAccess, random: Random, alreadyPicked: Collection<Goal>): ItemGoal {
        val goals = ArrayList(
            BuiltInRegistries.ITEM.stream()
                .filter { item ->
                    !BuiltInRegistries.ITEM.getTag(UNOBTAINABLE)
                        .orElseThrow()
                        .any { holder -> holder.value() == item }
                }
                .filter { item -> item !is SpawnEggItem }
                .filter { item -> item !is PotionItem }
                .filter { item -> item !is TippedArrowItem }
                .filter { item -> !MusicDiscGoal.test(item.defaultInstance) }
                .filter { item -> !PotterySherdGoal.test(item.defaultInstance) }
                .map<ItemGoal>(::SingleItemGoal)
                .toList()
        )

        for (potion in BuiltInRegistries.POTION) {
            val name = (potion as PotionAccessor).name

            if (name == null || !name.startsWith("long_") && !name.startsWith("strong_")) {
                goals.add(PotionGoal(potion))
            }
        }

        goals.add(MusicDiscGoal)
        goals.add(PotterySherdGoal)

        goals.removeAll(alreadyPicked)

        return goals.random(random)
    }

    fun itemToGoal(stack: ItemStack): ItemGoal {
        return if (MusicDiscGoal.test(stack)) {
            MusicDiscGoal
        } else if (PotterySherdGoal.test(stack)) {
            PotterySherdGoal
        } else {
            SingleItemGoal(stack.item)
        }
    }

    override fun name(): Component = Component.translatable("one_percent.item_goal")
}