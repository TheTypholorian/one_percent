package net.typho.one_percent.platform

import net.typho.one_percent.platform.services.PlatformHelper
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader

class NeoForgePlatformHelper : PlatformHelper {
    override fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return !FMLLoader.isProduction()
    }
}