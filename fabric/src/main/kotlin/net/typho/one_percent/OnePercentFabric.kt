package net.typho.one_percent

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.network.chat.Component
import net.typho.one_percent.goals.ItemGoal
import net.typho.one_percent.goals.ItemGoalManager
import net.typho.one_percent.session.sync.ClientboundSyncSessionPacket
import net.typho.one_percent.session.Session
import net.typho.one_percent.session.SessionStorage
import java.util.Optional
import kotlin.random.Random

object OnePercentFabric : ModInitializer {
    override fun onInitialize() {
        OnePercent.init()
        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            fun execute(item: ItemGoal, message: String, context: CommandContext<CommandSourceStack>) {
                context.source.sendSuccess({ Component.translatable("one_percent.$message", context.source.player?.name, item.getName()) }, true)
                val session = Session(item, context.source.level)
                (context.source.server.worldData as SessionStorage).`one_percent$setSession`(session)
                context.source.server.getPlayerList().broadcastAll(ClientboundSyncSessionPacket(Optional.of(session)))
            }

            dispatcher.register(
                Commands.literal("one_percent")
                    .then(
                        Commands.literal("daily")
                            .executes { context ->
                                val item = ItemGoalManager.pickGoal(context.source.level.registryAccess(), OnePercent.createDailyRandom())
                                execute(item, "start_daily", context)
                                return@executes 1
                            }
                    )
                    .then(
                        Commands.literal("random")
                            .executes { context ->
                                val item = ItemGoalManager.pickGoal(context.source.level.registryAccess(), Random)
                                execute(item, "start_random", context)
                                return@executes 1
                            }
                    )
                    .then(
                        Commands.argument("item", ItemArgument.item(context))
                            .executes { context ->
                                val item = ItemGoalManager.itemToGoal(ItemArgument.getItem(context, "item").item.defaultInstance)
                                execute(item, "start_specific", context)
                                return@executes 1
                            }
                    )
            )
        }
    }
}