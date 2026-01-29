package net.typho.one_percent

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(OnePercent.MOD_ID)
class OnePercentNeoForge(eventBus: IEventBus, modContainer: ModContainer) {
    init {
        OnePercent.init()
    }
}