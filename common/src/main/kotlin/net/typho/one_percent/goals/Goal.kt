package net.typho.one_percent.goals

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import io.netty.buffer.ByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface Goal {
    fun type(): ResourceLocation

    fun test(player: Player): Boolean

    fun getIcon(): ItemStack?

    fun getName(): Component

    fun getManager(): GoalManager<*>

    companion object {
        @JvmField
        val CODEC_MAP = HashMap<ResourceLocation, MapCodec<out Goal>>()
        @JvmField
        val STREAM_CODEC_MAP = HashMap<ResourceLocation, StreamCodec<ByteBuf, out Goal>>()
        @JvmField
        val CODEC: Codec<Goal> = ResourceLocation.CODEC.dispatch(
            { goal -> goal.type() },
            { type -> CODEC_MAP[type] }
        )
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, Goal> = ResourceLocation.STREAM_CODEC.dispatch(
            { goal -> goal.type() },
            { type -> STREAM_CODEC_MAP[type] }
        )

        init {
            CODEC_MAP[SingleItemGoal.TYPE] = SingleItemGoal.CODEC
            STREAM_CODEC_MAP[SingleItemGoal.TYPE] = SingleItemGoal.STREAM_CODEC

            CODEC_MAP[PotionGoal.TYPE] = PotionGoal.CODEC
            STREAM_CODEC_MAP[PotionGoal.TYPE] = PotionGoal.STREAM_CODEC

            CODEC_MAP[MusicDiscGoal.type()] = MapCodec.unit(MusicDiscGoal)
            STREAM_CODEC_MAP[MusicDiscGoal.type()] = StreamCodec.unit(MusicDiscGoal)

            CODEC_MAP[PotterySherdGoal.type()] = MapCodec.unit(PotterySherdGoal)
            STREAM_CODEC_MAP[PotterySherdGoal.type()] = StreamCodec.unit(PotterySherdGoal)
        }
    }
}