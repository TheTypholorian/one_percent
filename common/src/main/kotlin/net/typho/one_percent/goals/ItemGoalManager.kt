package net.typho.one_percent.goals

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.*
import net.typho.one_percent.OnePercent.UNOBTAINABLE
import net.typho.one_percent.mixin.goals.PotionAccessor

object ItemGoalManager : GoalManager<ItemGoal> {
    override fun getAllGoals(registries: RegistryAccess, alreadyPicked: Collection<Goal>): Collection<ItemGoal> {
        val goals = ArrayList(
            BuiltInRegistries.ITEM.stream()
                .filter { item ->
                    !BuiltInRegistries.ITEM.getTag(UNOBTAINABLE)
                        .orElseThrow()
                        .any { holder -> holder.value() == item }
                }
                .filter { item -> item != Items.AIR }
                .filter { item -> item !is SpawnEggItem }
                .filter { item -> item !is PotionItem }
                .filter { item -> item !is TippedArrowItem }
                .filter { item -> !MusicDiscGoal.test(item.defaultInstance) }
                .filter { item -> !BannerPatternGoal.test(item.defaultInstance) }
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
        goals.add(BannerPatternGoal)
        goals.add(PotterySherdGoal)

        goals.removeAll(alreadyPicked.toSet())

        return goals
    }

    fun itemToGoal(stack: ItemStack): ItemGoal {
        return if (MusicDiscGoal.test(stack)) {
            MusicDiscGoal
        } else if (BannerPatternGoal.test(stack)) {
            BannerPatternGoal
        } else if (PotterySherdGoal.test(stack)) {
            PotterySherdGoal
        } else {
            SingleItemGoal(stack.item)
        }
    }

    override fun name(): Component = Component.translatable("one_percent.item_goal")
}