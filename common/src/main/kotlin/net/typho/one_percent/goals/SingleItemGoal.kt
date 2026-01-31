package net.typho.one_percent.goals

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SmithingTemplateItem
import net.typho.one_percent.OnePercent
import net.typho.one_percent.mixin.goals.SmithingTemplateItemAccessor

data class SingleItemGoal(val item: Item) : ItemGoal {
    override fun test(stack: ItemStack) = stack.`is`(item)

    override fun type() = TYPE

    override fun getIcon(): ItemStack = item.defaultInstance

    override fun getName(): Component {
        return if (item is SmithingTemplateItem) {
            (item as SmithingTemplateItemAccessor).upgradeDescription.plainCopy()
        } else {
            Component.translatable(item.descriptionId)
        }
    }

    override fun toString() = "SingleItemGoal[$item]"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SingleItemGoal

        return item == other.item
    }

    override fun hashCode(): Int {
        return item.hashCode()
    }

    companion object {
        @JvmField
        val TYPE = OnePercent.id("single_item")
        @JvmField
        val CODEC: MapCodec<SingleItemGoal> = RecordCodecBuilder.mapCodec {
            it.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter { goal -> goal.item }
            ).apply(it, ::SingleItemGoal)
        }
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, SingleItemGoal> = StreamCodec.composite(
            ByteBufCodecs.fromCodecTrusted(BuiltInRegistries.ITEM.byNameCodec()), { goal -> goal.item },
            ::SingleItemGoal
        )
    }
}