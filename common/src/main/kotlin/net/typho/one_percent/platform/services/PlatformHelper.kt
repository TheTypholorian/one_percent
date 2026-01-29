package net.typho.one_percent.platform.services

interface PlatformHelper {
    fun isModLoaded(modId: String?): Boolean

    fun isDevelopmentEnvironment(): Boolean
}