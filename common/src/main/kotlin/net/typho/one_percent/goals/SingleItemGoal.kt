package net.typho.one_percent.goals

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.typho.one_percent.OnePercent

data class SingleItemGoal(val item: Item) : ItemGoal {
    override fun test(stack: ItemStack) = stack.`is`(item)

    override fun type() = TYPE

    override fun getIcon(): ItemStack = item.defaultInstance

    override fun getName(): Component = Component.translatable(item.descriptionId)

    override fun toString() = "SingleItemGoal[$item]"

    companion object {
        @JvmField
        val TYPE = OnePercent.id("single_item")
        @JvmField
        val CODEC: MapCodec<SingleItemGoal> = RecordCodecBuilder.mapCodec {
            it.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter { goal -> goal.item }
            ).apply(it, ::SingleItemGoal)
        }
    }
}