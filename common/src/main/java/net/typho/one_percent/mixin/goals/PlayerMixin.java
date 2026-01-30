package net.typho.one_percent.mixin.goals;

import net.minecraft.world.entity.player.Player;
import net.typho.one_percent.session.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void tick(CallbackInfo ci) {
        // TODO
        Player player = (Player) (Object) this;
        Session session = null;//OnePercent.CURRENT_SESSION;

        if (session != null) {
            if (session.goal.test(player)) {
                session.end(player);
                //OnePercent.CURRENT_SESSION = null;
            }
        }
    }
}
