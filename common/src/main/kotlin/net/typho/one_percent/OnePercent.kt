package net.typho.one_percent

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.PacketType
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.GameRules
import net.typho.one_percent.goals.ItemGoal
import net.typho.one_percent.goals.ItemGoalManager
import net.typho.one_percent.mixin.session.GameRulesAccessor
import net.typho.one_percent.mixin.session.GameRulesIntegerValueAccessor
import net.typho.one_percent.session.Session
import net.typho.one_percent.session.SessionStorage
import net.typho.one_percent.session.sync.ClientboundSyncSessionPacket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.util.*
import kotlin.random.Random
import kotlin.random.asKotlinRandom

object OnePercent {
    const val MOD_ID = "one_percent"
    const val MOD_NAME = "One Percent"
    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)

    @JvmField
    val UNOBTAINABLE: TagKey<Item> = TagKey.create(Registries.ITEM, id("unobtainable"))

    @JvmField
    val CLIENTBOUND_SYNC_SESSION_PACKET = PacketType<ClientboundSyncSessionPacket>(PacketFlow.CLIENTBOUND, id("sync_session"))

    @JvmField
    val REQUIRED_SCORE: GameRules.Key<GameRules.IntegerValue> = GameRulesAccessor.register(
        "onePercentRequiredScore",
        GameRules.Category.MISC,
        GameRulesIntegerValueAccessor.create(5)
    )

    fun createDailyRandom(): Random = java.util.Random(LocalDate.now().toEpochDay()).asKotlinRandom()

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)

    @JvmStatic
    fun registerCommands(dispatcher: CommandDispatcher<CommandSourceStack>, context: CommandBuildContext) {
        fun sync(context: CommandContext<CommandSourceStack>) {
            context.source.server.playerList.broadcastAll(ClientboundSyncSessionPacket(Optional.ofNullable((context.source.server.worldData as SessionStorage).`one_percent$getSession`())))
        }

        fun execute(item: ItemGoal, message: String, context: CommandContext<CommandSourceStack>) {
            context.source.sendSuccess({ Component.translatable("one_percent.$message", context.source.player?.name, item.getName()) }, true)
            val session = Session(item, context.source.level)
            (context.source.server.worldData as SessionStorage).`one_percent$setSession`(session)
            SessionStorage.saveSession(context.source.server)
            sync(context)
        }

        dispatcher.register(
            Commands.literal("one_percent")
                .then(
                    Commands.literal("daily")
                        .executes { context ->
                            val item = ItemGoalManager.getAllGoals(context.source.level.registryAccess()).random(createDailyRandom())
                            execute(item, "start_daily", context)
                            return@executes 1
                        }
                )
                .then(
                    Commands.literal("random")
                        .executes { context ->
                            val item = ItemGoalManager.getAllGoals(context.source.level.registryAccess()).random()
                            execute(item, "start_random", context)
                            return@executes 1
                        }
                )
                .then(
                    Commands.literal("specific")
                        .then(
                            Commands.argument("item", ItemArgument.item(context))
                                .executes { context ->
                                    val item = ItemGoalManager.itemToGoal(ItemArgument.getItem(context, "item").item.defaultInstance)
                                    execute(item, "start_specific", context)
                                    return@executes 1
                                }
                        )
                )
                .then(
                    Commands.literal("reroll")
                        .executes { context ->
                            val session = (context.source.server.worldData as SessionStorage).`one_percent$getSession`()
                            session?.goal = ItemGoalManager.getAllGoals(
                                context.source.level.registryAccess(),
                                session.alreadyPicked
                            ).random()
                            sync(context)
                            return@executes 1
                        }
                )
                .then(
                    Commands.literal("set_score")
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .then(
                                    Commands.argument("score", IntegerArgumentType.integer(0))
                                        .executes { context ->
                                            val target = EntityArgument.getPlayer(context, "target")
                                            val score = IntegerArgumentType.getInteger(context, "score")
                                            (context.source.server.worldData as SessionStorage).`one_percent$getSession`()?.scores[target.stringUUID] = score
                                            context.source.sendSuccess({ Component.translatable("one_percent.set_score", context.source.player?.name, score) }, true)
                                            sync(context)
                                            return@executes 1
                                        }
                                )
                        )
                )
        )
    }

    fun init() {
    }
}