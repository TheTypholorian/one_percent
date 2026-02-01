package net.typho.one_percent.goals

import net.minecraft.network.chat.Component
import net.minecraft.world.item.BannerPatternItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.typho.one_percent.OnePercent

object BannerPatternGoal : ItemGoal {
    override fun test(stack: ItemStack) = stack.item is BannerPatternItem

    override fun type() = OnePercent.id("banner_pattern")

    override fun getIcon(): ItemStack = Items.MOJANG_BANNER_PATTERN.defaultInstance

    override fun getName(): Component = Component.translatable("one_percent.banner_pattern")

    override fun toString() = "BannerPatternGoal"
}