package net.typho.one_percent

import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.util.RandomSource
import net.minecraft.world.item.Item
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.stream.Stream

object OnePercent {
    const val MOD_ID = "one_percent"
    const val MOD_NAME = "One Percent"
    @JvmField
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_NAME)
    @JvmField
    val UNOBTAINABLE: TagKey<Item> = TagKey.create(Registries.ITEM, id("unobtainable"))

    fun pickRandomItem(registries: RegistryAccess, random: RandomSource): Item {
        val items = registries.registryOrThrow(Registries.ITEM)

        return items.stream()
            .filter { item ->
                !items.getTag(UNOBTAINABLE)
                    .orElseThrow()
                    .any { holder -> holder.value() == item }
            }
            .sorted(Comparator.comparingDouble { random.nextDouble() })
            .findFirst()
            .orElseThrow()
    }

    fun init() {
    }

    fun id(path: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
}