package net.typho.one_percent.goals

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.Potion
import net.minecraft.world.item.alchemy.PotionContents
import net.typho.one_percent.OnePercent
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

class PotionGoal(val potion: Potion) : ItemGoal {
    override fun test(stack: ItemStack): Boolean = stack.get(DataComponents.POTION_CONTENTS)?.potion?.getOrNull()?.value() == potion

    override fun type() = TYPE

    override fun getIcon(): ItemStack {
        val stack = ItemStack(Items.POTION)
        stack.set(DataComponents.POTION_CONTENTS, PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)))
        return stack
    }

    override fun getName(): Component = Component.translatable(Potion.getName(Optional.of(BuiltInRegistries.POTION.wrapAsHolder(potion)), "item.minecraft.potion.effect."))

    override fun toString() = "PotionGoal[${BuiltInRegistries.POTION.getKey(potion)}]"

    companion object {
        @JvmField
        val TYPE = OnePercent.id("potion")
        @JvmField
        val CODEC: MapCodec<PotionGoal> = RecordCodecBuilder.mapCodec {
            it.group(
                BuiltInRegistries.POTION.byNameCodec().fieldOf("potion").forGetter { goal -> goal.potion }
            ).apply(it, ::PotionGoal)
        }
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, PotionGoal> = StreamCodec.composite(
            ByteBufCodecs.fromCodecTrusted(BuiltInRegistries.POTION.byNameCodec()), { goal -> goal.potion },
            ::PotionGoal
        )
    }
}