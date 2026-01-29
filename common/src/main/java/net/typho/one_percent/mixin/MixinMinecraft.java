package net.typho.one_percent.mixin;

import net.typho.one_percent.OnePercent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(at = @At("TAIL"), method = "<init>")
    private void init(CallbackInfo info) {
        OnePercent.LOGGER.info("This line is printed by an example mod common mixin!");
        OnePercent.LOGGER.info("MC Version: {}", Minecraft.getInstance().getVersionType());
    }
}