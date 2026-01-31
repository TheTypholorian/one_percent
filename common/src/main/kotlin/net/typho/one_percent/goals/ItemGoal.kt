package net.typho.one_percent.goals

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface ItemGoal : Goal {
    override fun test(player: Player) = player.inventory.hasAnyMatching { stack -> test(stack) }

    fun test(stack: ItemStack): Boolean

    override fun getManager() = ItemGoalManager
}