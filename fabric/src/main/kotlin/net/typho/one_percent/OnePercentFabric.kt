package net.typho.one_percent

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.typho.one_percent.goals.ItemGoalManager
import kotlin.random.Random

object OnePercentFabric : ModInitializer {
    override fun onInitialize() {
        OnePercent.init()
        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            dispatcher.register(
                Commands.literal("one_percent")
                    .executes { context ->
                        try {
                            val item = ItemGoalManager.pickGoal(context.source.level.registryAccess(), Random)
                            context.source.sendSuccess({ Component.literal("Picked $item") }, true)
                            return@executes 1
                        } catch (e: RuntimeException) {
                            e.printStackTrace()
                            throw e
                        }
                    }
            )
        }
    }
}