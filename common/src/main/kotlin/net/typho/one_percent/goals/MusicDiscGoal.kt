package net.typho.one_percent.goals

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.typho.one_percent.OnePercent

object MusicDiscGoal : ItemGoal {
    override fun test(stack: ItemStack) = stack.get(DataComponents.JUKEBOX_PLAYABLE) != null

    override fun type() = OnePercent.id("music_disc")

    override fun getIcon(): ItemStack = Items.MUSIC_DISC_OTHERSIDE.defaultInstance

    override fun getName(): Component = Component.translatable("one_percent.music_disc")

    override fun toString() = "MusicDiscGoal"
}