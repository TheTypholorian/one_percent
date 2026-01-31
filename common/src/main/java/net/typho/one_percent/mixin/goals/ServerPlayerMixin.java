package net.typho.one_percent.mixin.goals;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.typho.one_percent.session.Session;
import net.typho.one_percent.session.SessionStorage;
import net.typho.one_percent.session.sync.ClientboundSyncSessionPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tick(CallbackInfo ci) {
        Player player = (Player) (Object) this;
        SessionStorage storage = (SessionStorage) server.getWorldData();
        Session session = storage.one_percent$getSession();

        if (session != null && session.goal.test(player)) {
            if (session.point(player, server)) {
                storage.one_percent$setSession(null);
            }

            SessionStorage.saveSession(server);
            server.getPlayerList().broadcastAll(new ClientboundSyncSessionPacket(Optional.ofNullable(storage.one_percent$getSession())));
        }
    }
}
