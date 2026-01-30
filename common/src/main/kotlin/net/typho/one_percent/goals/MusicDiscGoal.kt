package net.typho.one_percent.goals

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

object MusicDiscGoal : ItemGoal {
    override fun test(stack: ItemStack) = stack.get(DataComponents.JUKEBOX_PLAYABLE) != null

    override fun getName(): Component = Component.translatable("one_percent.music_disc")

    override fun toString() = "MusicDiscGoal"
}