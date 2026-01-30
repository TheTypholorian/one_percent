package net.typho.one_percent

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.network.chat.Component
import net.typho.one_percent.goals.ItemGoalManager

object OnePercentFabric : ModInitializer {
    override fun onInitialize() {
        OnePercent.init()
        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            dispatcher.register(
                Commands.literal("one_percent")
                    .executes { context ->
                        try {
                            val item = ItemGoalManager.pickGoal(context.source.level.registryAccess(), OnePercent.createDailyRandom())
                            context.source.sendSuccess({ Component.translatable("one_percent.start", item.getName()) }, true)
                            // TODO
                            //OnePercent.CURRENT_SESSION = Session(item, context.source.level)
                            return@executes 1
                        } catch (e: RuntimeException) {
                            e.printStackTrace()
                            throw e
                        }
                    }
                    .then(
                        Commands.argument("item", ItemArgument.item(context))
                            .executes { context ->
                                val item = ItemGoalManager.itemToGoal(ItemArgument.getItem(context, "item").item.defaultInstance)
                                context.source.sendSuccess({ Component.translatable("one_percent.start_set", context.source.player?.name, item.getName()) }, true)
                                //OnePercent.CURRENT_SESSION = Session(item, context.source.level)
                                return@executes 1
                            }
                    )
            )
        }
    }
}