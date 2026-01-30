package net.typho.one_percent.session

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.typho.one_percent.goals.Goal

data class Session(
    @JvmField
    val goal: Goal,
    @JvmField
    val startGameTime: Long,
    @JvmField
    val startIRLTime: Long
) {
    constructor(goal: Goal, level: Level) : this(goal, level.gameTime, System.currentTimeMillis())

    fun getWinMessage(winner: Player): Component {
        val deltaGameTime = winner.level().gameTime - startGameTime
        val deltaIRLTime = System.currentTimeMillis() - startIRLTime

        return Component.translatable(
            "one_percent.win",
            winner.name,
            goal.getName(),
            secondsToTime(deltaIRLTime / 1000).copy().withStyle(ChatFormatting.AQUA),
            secondsToTime(deltaGameTime / 20).copy().withStyle(ChatFormatting.YELLOW)
        )
    }

    fun end(winner: Player) {
        val message = getWinMessage(winner)

        if (winner.level().isClientSide && Minecraft.getInstance().isSingleplayer) {
            winner.displayClientMessage(message, false)
        } else {
            winner.level().server?.sendSystemMessage(message)
        }
    }

    companion object {
        @JvmStatic
        fun secondsToTime(seconds: Long): Component {
            val hours = seconds / 3600
            val minutes = (seconds % 3600) / 60
            val clampedSeconds = seconds % 60

            return when {
                hours > 0 -> Component.translatable(
                    "one_percent.time.hours",
                    hours,
                    "%02d".format(minutes),
                    "%02d".format(clampedSeconds)
                )
                minutes > 0 -> Component.translatable(
                    "one_percent.time.minutes",
                    minutes,
                    "%02d".format(clampedSeconds)
                )
                else -> Component.translatable("one_percent.time.seconds", "%02d".format(clampedSeconds))
            }
        }
    }
}
