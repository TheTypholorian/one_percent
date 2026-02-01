package net.typho.one_percent.mixin.fabric.qol;

import com.mojang.datafixers.util.Function9;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ConcentricRingsStructurePlacement.class)
public class ConcentricRingsStructurePlacementMixin {
    @ModifyArg(
            method = "method_40167",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/datafixers/Products$P9;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/util/Function9;)Lcom/mojang/datafixers/kinds/App;"
            ),
            remap = false,
            index = 1
    )
    @SuppressWarnings("unchecked")
    private static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> codec(Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> function) {
        return (t1, t2, t3, t4, t5, t6, t7, t8, t9) -> function.apply(t1, t2, t3, t4, t5, (T6) (Integer) (((Integer) t6) / 4), t7, t8, t9);
    }
}
