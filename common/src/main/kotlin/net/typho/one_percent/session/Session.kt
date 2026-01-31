package net.typho.one_percent.session

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.core.UUIDUtil
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.MinecraftServer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.typho.one_percent.OnePercent
import net.typho.one_percent.goals.Goal
import java.util.*
import kotlin.random.Random

data class Session(
    @JvmField
    var goal: Goal,
    @JvmField
    var startGameTime: Long,
    @JvmField
    var startIRLTime: Long,
    @JvmField
    val scores: MutableMap<UUID, Int> = HashMap()
) {
    constructor(goal: Goal, level: Level) : this(goal, level.gameTime, System.currentTimeMillis())

    fun getWinMessage(winner: Player): Component {
        val deltaGameTime = winner.level().gameTime - startGameTime
        val deltaIRLTime = System.currentTimeMillis() - startIRLTime

        return Component.translatable(
            "one_percent.point",
            winner.name,
            goal.getName(),
            secondsToTime(deltaIRLTime / 1000).copy().withStyle(ChatFormatting.AQUA),
            secondsToTime(deltaGameTime / 20).copy().withStyle(ChatFormatting.YELLOW)
        )
    }

    fun point(winner: Player, server: MinecraftServer): Boolean {
        server.playerList.broadcastSystemMessage(getWinMessage(winner), false)
        // TODO impl sound
        //winner.level().playSound(null, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 1f, 1f)

        if (server.playerList.playerCount > 1 || scores.isNotEmpty()) {
            startGameTime = server.overworld().gameTime
            startIRLTime = System.currentTimeMillis()
            goal = goal.getManager().pickGoal(server.registryAccess(), Random)

            val score = scores.compute(winner.uuid) { key, value -> (value ?: 0) + 1 }

            return (score ?: 0) >= server.gameRules.getInt(OnePercent.REQUIRED_SCORE)
        }

        return true
    }

    companion object {
        @JvmField
        val CODEC: MapCodec<Session> = RecordCodecBuilder.mapCodec {
            it.group(
                Goal.CODEC.fieldOf("goal").forGetter { session -> session.goal },
                Codec.LONG.fieldOf("startGameTime").forGetter { session -> session.startGameTime },
                Codec.LONG.fieldOf("startIRLTime").forGetter { session -> session.startIRLTime },
                Codec.unboundedMap(UUIDUtil.CODEC, Codec.INT).fieldOf("scores").forGetter { session -> session.scores }
            ).apply(it, ::Session)
        }
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, Session> = StreamCodec.composite(
            Goal.STREAM_CODEC, { session -> session.goal },
            ByteBufCodecs.VAR_LONG, { session -> session.startGameTime },
            ByteBufCodecs.VAR_LONG, { session -> session.startIRLTime },
            ByteBufCodecs.map(::HashMap, UUIDUtil.STREAM_CODEC, ByteBufCodecs.VAR_INT), { session -> session.scores },
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
