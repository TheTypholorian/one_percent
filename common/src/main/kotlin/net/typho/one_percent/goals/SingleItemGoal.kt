package net.typho.one_percent.goals

import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

data class SingleItemGoal(val item: Item) : ItemGoal {
    override fun test(stack: ItemStack) = stack.`is`(item)

    override fun getName(): Component = Component.translatable(item.descriptionId)

    override fun toString() = "SingleItemGoal[$item]"
}