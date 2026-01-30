package net.typho.one_percent.goals

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.DecoratedPotPatterns

object PotterySherdGoal : ItemGoal {
    override fun test(stack: ItemStack) = DecoratedPotPatterns.getPatternFromItem(stack.item) != null

    override fun getName(): Component = Component.translatable("one_percent.pottery_sherd")

    override fun toString() = "PotterySherdGoal"
}