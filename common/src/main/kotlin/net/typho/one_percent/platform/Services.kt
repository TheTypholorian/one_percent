package net.typho.one_percent.platform

import net.typho.one_percent.platform.services.PlatformHelper
import java.util.*

object Services {
    val PLATFORM = load(PlatformHelper::class.java)

    fun <T> load(clazz: Class<T>): T {
        return ServiceLoader.load<T>(clazz)
            .findFirst()
            .orElseThrow {
                IllegalStateException("Failed to load service for ${clazz.name}")
            }
    }
}