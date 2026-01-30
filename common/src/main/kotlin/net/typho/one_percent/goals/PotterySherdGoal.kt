package net.typho.one_percent.goals

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.DecoratedPotPatterns
import net.typho.one_percent.OnePercent

object PotterySherdGoal : ItemGoal {
    override fun test(stack: ItemStack) = DecoratedPotPatterns.getPatternFromItem(stack.item) != null

    override fun type() = OnePercent.id("pottery_sherd")

    override fun getIcon(): ItemStack = Items.ARMS_UP_POTTERY_SHERD.defaultInstance

    override fun getName(): Component = Component.translatable("one_percent.pottery_sherd")

    override fun toString() = "PotterySherdGoal"
}