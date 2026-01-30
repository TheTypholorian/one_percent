package net.typho.one_percent.goals

import com.mojang.serialization.MapCodec
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface Goal {
    fun type(): ResourceLocation

    fun test(player: Player): Boolean

    fun getIcon(): ItemStack?

    fun getName(): Component

    companion object {
        @JvmField
        val REGISTRY = HashMap<ResourceLocation, MapCodec<out Goal>>()
        @JvmField
        val CODEC: MapCodec<Goal> = ResourceLocation.CODEC.dispatchMap(
            { goal -> goal.type() },
            { type -> REGISTRY[type] }
        )

        init {
            REGISTRY[SingleItemGoal.TYPE] = SingleItemGoal.CODEC
            REGISTRY[MusicDiscGoal.type()] = MapCodec.unit(MusicDiscGoal)
            REGISTRY[PotterySherdGoal.type()] = MapCodec.unit(PotterySherdGoal)
        }
    }
}