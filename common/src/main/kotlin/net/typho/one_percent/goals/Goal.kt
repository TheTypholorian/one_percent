package net.typho.one_percent.goals

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

interface Goal {
    fun test(player: Player): Boolean

    fun getName(): Component
}