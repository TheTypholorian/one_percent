package net.typho.one_percent.session

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.typho.one_percent.goals.Goal

data class Session(
    @JvmField
    var goal: Goal,
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

    fun end(winner: Player, server: MinecraftServer) {
        server.playerList.broadcastSystemMessage(getWinMessage(winner), false)
        // TODO impl sound
        //winner.level().playSound(null, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1f, 1f)
    }

    companion object {
        @JvmField
        val CODEC: MapCodec<Session> = RecordCodecBuilder.mapCodec {
            it.group(
                Goal.CODEC.fieldOf("goal").forGetter { session -> session.goal },
                Codec.LONG.fieldOf("startGameTime").forGetter { session -> session.startGameTime },
                Codec.LONG.fieldOf("startIRLTime").forGetter { session -> session.startIRLTime },
            ).apply(it, ::Session)
        }
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, Session> = StreamCodec.composite(
            Goal.STREAM_CODEC, { session -> session.goal },
            ByteBufCodecs.VAR_LONG, { session -> session.startGameTime },
            ByteBufCodecs.VAR_LONG, { session -> session.startIRLTime },
            ::Session
        )

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
