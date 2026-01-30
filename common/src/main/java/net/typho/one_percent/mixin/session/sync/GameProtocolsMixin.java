package net.typho.one_percent.mixin.session.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.ProtocolInfoBuilder;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.GameProtocols;
import net.typho.one_percent.OnePercent;
import net.typho.one_percent.session.sync.ClientboundSyncSessionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Consumer;

@Mixin(GameProtocols.class)
public class GameProtocolsMixin {
    @ModifyArg(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/ProtocolInfoBuilder;clientboundProtocol(Lnet/minecraft/network/ConnectionProtocol;Ljava/util/function/Consumer;)Lnet/minecraft/network/ProtocolInfo$Unbound;"
            ),
            index = 1
    )
    private static <T extends ClientGamePacketListener, B extends RegistryFriendlyByteBuf> Consumer<ProtocolInfoBuilder<T, B>> clientboundProtocol(Consumer<ProtocolInfoBuilder<T, B>> setup) {
        return setup.andThen(builder -> builder.addPacket(OnePercent.CLIENTBOUND_SYNC_SESSION_PACKET, ClientboundSyncSessionPacket.STREAM_CODEC));
    }
}
