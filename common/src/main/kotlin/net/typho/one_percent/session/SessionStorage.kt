package net.typho.one_percent.session

import net.minecraft.server.MinecraftServer
import net.typho.one_percent.mixin.session.save.MinecraftServerAccessor

interface SessionStorage {
    fun `one_percent$getSession`(): Session?

    fun `one_percent$setSession`(session: Session?)

    companion object {
        @JvmStatic
        fun saveSession(server: MinecraftServer) {
            (server as MinecraftServerAccessor).storageSource.saveDataTag(
                server.registryAccess(),
                server.worldData,
                server.playerList.singleplayerData
            )
        }
    }
}