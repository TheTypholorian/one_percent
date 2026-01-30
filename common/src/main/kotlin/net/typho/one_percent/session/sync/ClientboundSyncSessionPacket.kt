package net.typho.one_percent.session.sync

import io.netty.buffer.ByteBuf
import net.minecraft.client.Minecraft
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.typho.one_percent.OnePercent
import net.typho.one_percent.session.Session
import net.typho.one_percent.session.SessionStorage
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

data class ClientboundSyncSessionPacket(val session: Optional<Session>) : Packet<ClientGamePacketListener> {
    companion object {
        @JvmField
        val STREAM_CODEC: StreamCodec<ByteBuf, ClientboundSyncSessionPacket> = StreamCodec.composite(
            ByteBufCodecs.optional(Session.STREAM_CODEC), { packet -> packet.session },
            ::ClientboundSyncSessionPacket
        )
    }

    override fun type() = OnePercent.CLIENTBOUND_SYNC_SESSION_PACKET

    override fun handle(listener: ClientGamePacketListener) {
        (Minecraft.getInstance() as SessionStorage).`one_percent$setSession`(session.getOrNull())
    }
}