package net.typho.one_percent

import net.fabricmc.api.ModInitializer

object OnePercentFabric : ModInitializer {
    override fun onInitialize() {
        OnePercent.init()
    }
}