package net.typho.one_percent.mixin.session;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.typho.one_percent.session.Session;
import net.typho.one_percent.session.SessionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    public abstract Font getFont();

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        Session session = ((SessionStorage) Minecraft.getInstance()).one_percent$getSession();

        if (session != null) {
            PoseStack stack = guiGraphics.pose();

            stack.pushPose();
            stack.scale(2, 2, 2);

            ItemStack icon = session.goal.getIcon();
            int textX = icon == null ? 4 : 24;

            if (icon != null) {
                guiGraphics.renderFakeItem(icon, 4, 4);
            }

            guiGraphics.drawString(getFont(), session.goal.getName().copy(), textX, 9, 0xFFFFFFFF);

            stack.popPose();

            guiGraphics.drawString(getFont(), Component.translatable("one_percent.objective").withStyle(ChatFormatting.BOLD), textX * 2, 4 * 2, Objects.requireNonNull(ChatFormatting.YELLOW.getColor()));

            Component irl = Session.secondsToTime((System.currentTimeMillis() - session.startIRLTime) / 1000);
            Component game = Session.secondsToTime((minecraft.level.getGameTime() - session.startGameTime) / 20);

            guiGraphics.drawString(getFont(), irl, textX * 2, 18 * 2, Objects.requireNonNull(ChatFormatting.AQUA.getColor()));
            guiGraphics.drawString(getFont(), game, textX * 2 + getFont().width(irl) + 4, 18 * 2, Objects.requireNonNull(ChatFormatting.YELLOW.getColor()));

            int textY = 18 * 2 + getFont().lineHeight * 2;

            session.scores.entrySet().stream()
                    .sorted(Comparator.comparingInt(Map.Entry::getValue))
                    .forEachOrdered(entry -> {
                        Player player = minecraft.level.getPlayerByUUID(UUID.fromString(entry.getKey()));

                        if (player != null) {
                            guiGraphics.drawString(
                                    getFont(),
                                    Component.translatable("one_percent.score", player.getDisplayName(), entry.getValue()),
                                    4 * 2,
                                    textY,
                                    0xFFFFFFFF
                            );
                        }
                    });
        }
    }
}
