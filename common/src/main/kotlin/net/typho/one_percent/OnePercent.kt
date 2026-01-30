package net.typho.one_percent

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.typho.one_percent.session.Session
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate
import java.time.ZoneOffset
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
    var CURRENT_SESSION: Session? = null

    fun createDailyRandom(): Random = java.util.Random(LocalDate.now(ZoneOffset.UTC).toEpochDay()).asKotlinRandom()

    fun init() {
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}