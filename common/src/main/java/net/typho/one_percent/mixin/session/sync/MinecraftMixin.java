package net.typho.one_percent.mixin.session.sync;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.typho.one_percent.session.Session;
import net.typho.one_percent.session.SessionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin implements SessionStorage {
    @Unique
    private Session one_percent$session;

    @Override
    public @Nullable Session one_percent$getSession() {
        return one_percent$session;
    }

    @Override
    public void one_percent$setSession(@Nullable Session session) {
        one_percent$session = session;
    }

    @Inject(
            method = "disconnect(Lnet/minecraft/client/gui/screens/Screen;Z)V",
            at = @At("TAIL")
    )
    private void disconnect(Screen nextScreen, boolean keepResourcePacks, CallbackInfo ci) {
        one_percent$session = null;
    }
}
