package net.typho.one_percent.mixin.session;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.typho.one_percent.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    public abstract Font getFont();

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        // TODO
        Session session = null;//Minecraft.getInstance().level.getLevelData().;

        if (session != null) {
            PoseStack stack = guiGraphics.pose();

            stack.pushPose();
            stack.scale(2, 2, 2);

            ItemStack icon = session.goal.getIcon();

            if (icon != null) {
                guiGraphics.renderFakeItem(icon, 4, 4);
            }

            guiGraphics.drawString(getFont(), session.goal.getName(), icon == null ? 4 : 24, 4, 0xFFFFFFFF);

            stack.popPose();
        }
    }
}
