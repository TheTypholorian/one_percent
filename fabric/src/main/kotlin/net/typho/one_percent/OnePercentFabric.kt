package net.typho.one_percent

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource

object OnePercentFabric : ModInitializer {
    override fun onInitialize() {
        OnePercent.init()
        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            dispatcher.register(
                Commands.literal("one_percent")
                    .executes { context ->
                        context.source.sendSuccess({ Component.literal("Picked ${OnePercent.pickRandomItem(context.source.level.registryAccess(), RandomSource.create())}") }, true)
                        return@executes 1
                    }
            )
        }
    }
}