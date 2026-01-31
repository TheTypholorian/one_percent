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
    var goal: Goal,
    @JvmField
    var startGameTime: Long,
    @JvmField
    var startIRLTime: Long,
    scores: Map<String, Int> = emptyMap(),
    alreadyPicked: Collection<Goal> = emptySet(),
    @JvmField
    val seed: Long? = null,
) {
    @JvmField
    val scores = HashMap(scores)
    @JvmField
    val alreadyPicked = ArrayList(alreadyPicked)

    constructor(goal: Goal, level: ServerLevel) : this(goal, level.gameTime, System.currentTimeMillis(), seed = Random.nextLong())

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

        //if (server.playerList.playerCount > 1 || scores.isNotEmpty()) {
            alreadyPicked.add(goal)
            startGameTime = server.overworld().gameTime
            startIRLTime = System.currentTimeMillis()
            goal = goal.getManager().pickGoal(server.registryAccess(), Random(seed!!), alreadyPicked)

            val score = scores.compute(winner.stringUUID) { key, value -> (value ?: 0) + 1 }

            return (score ?: 0) >= server.gameRules.getInt(OnePercent.REQUIRED_SCORE)
        //}

        //return true
    }

    companion object {
        @JvmField
        val CODEC: Codec<Session> = RecordCodecBuilder.create {
            it.group(
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
