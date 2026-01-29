package net.typho.one_percent.platform

import net.typho.one_percent.platform.services.PlatformHelper
import net.fabricmc.loader.api.FabricLoader

class FabricPlatformHelper : PlatformHelper {
    override fun isModLoaded(modId: String?): Boolean {
        return FabricLoader.getInstance().isModLoaded(modId)
    }

    override fun isDevelopmentEnvironment(): Boolean {
        return FabricLoader.getInstance().isDevelopmentEnvironment
    }
}