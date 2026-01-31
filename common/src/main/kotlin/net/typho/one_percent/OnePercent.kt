package net.typho.one_percent

import net.minecraft.core.registries.Registries
import net.minecraft.network.protocol.PacketFlow
import net.minecraft.network.protocol.PacketType
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.GameRules
import net.typho.one_percent.mixin.session.GameRulesAccessor
import net.typho.one_percent.mixin.session.GameRulesIntegerValueAccessor
import net.typho.one_percent.session.sync.ClientboundSyncSessionPacket
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
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
        "one_percent_required_score",
        GameRules.Category.MISC,
        GameRulesIntegerValueAccessor.create(5)
    )

    fun createDailyRandom(): Random = java.util.Random(LocalDate.now().toEpochDay()).asKotlinRandom()

    fun init() {
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}