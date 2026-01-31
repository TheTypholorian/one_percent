package net.typho.one_percent.session

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.typho.one_percent.OnePercent
import net.typho.one_percent.goals.Goal
import kotlin.random.Random

class Session(
    @JvmField
    val multiplayer: Boolean,
    @JvmField
    var goal: Goal,
    @JvmField
    val startGameTime: Long,
    @JvmField
    val startIRLTime: Long,
    scores: Map<String, Int> = emptyMap(),
    alreadyPicked: Collection<Goal> = emptySet(),
    @JvmField
    val seed: Long? = null,
) {
    @JvmField
    val scores = HashMap(scores)
    @JvmField
    val alreadyPicked = ArrayList(alreadyPicked)

    constructor(goal: Goal, level: ServerLevel) : this(level.server.playerCount > 1, goal, level.gameTime, System.currentTimeMillis(), seed = Random.nextLong())

    fun getWinMessage(winner: Player): Component {
        if (multiplayer) {
            return Component.translatable(
                "one_percent.win",
                winner.name
            )
        } else {
            val deltaGameTime = winner.level().gameTime - startGameTime
            val deltaIRLTime = System.currentTimeMillis() - startIRLTime

            return Component.translatable(
                "one_percent.win_singleplayer",
                winner.name,
                goal.getName(),
                secondsToTime(deltaIRLTime / 1000).copy().withStyle(ChatFormatting.AQUA),
                secondsToTime(deltaGameTime / 20).copy().withStyle(ChatFormatting.YELLOW)
            )
        }
    }

    fun getPointMessage(winner: Player, score: Int): Component {
        return Component.translatable(
            "one_percent.point",
            winner.name,
            goal.getName(),
            score
        )
    }

    fun point(winner: Player, server: MinecraftServer): Boolean {
        if (multiplayer) {
            val score = scores.compute(winner.stringUUID) { key, value -> (value ?: 0) + 1 }!!
            val won = score >= server.gameRules.getInt(OnePercent.REQUIRED_SCORE)

            server.playerList.broadcastSystemMessage(if (won) getWinMessage(winner) else getPointMessage(winner, score), false)

            alreadyPicked.add(goal)
            goal = goal.getManager().pickGoal(server.registryAccess(), Random(seed!!), alreadyPicked)

            return won
        } else {
            server.playerList.broadcastSystemMessage(getWinMessage(winner), false)

            return true
        }
    }

    companion object {
        @JvmField
        val CODEC: Codec<Session> = RecordCodecBuilder.create {
            it.group(
                Codec.BOOL.fieldOf("multiplayer").forGetter { session -> session.multiplayer },
                Goal.CODEC.fieldOf("goal").forGetter { session -> session.goal },
                Codec.LONG.fieldOf("startGameTime").forGetter { session -> session.startGameTime },
                Codec.LONG.fieldOf("startIRLTime").forGetter { session -> session.startIRLTime },
                Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("scores").forGetter { session -> session.scores },
                Codec.list(Goal.CODEC).optionalFieldOf("alreadyPicked", listOf()).forGetter { session -> session.alreadyPicked },
                Codec.LONG.fieldOf("seed").forGetter { session -> session.seed }
            ).apply(it, ::Session)
        }
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, Session> = StreamCodec.composite(
            ByteBufCodecs.BOOL, { session -> session.multiplayer },
            Goal.STREAM_CODEC, { session -> session.goal },
            ByteBufCodecs.VAR_LONG, { session -> session.startGameTime },
            ByteBufCodecs.VAR_LONG, { session -> session.startIRLTime },
            ByteBufCodecs.map(::HashMap, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT), { session -> session.scores },
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
