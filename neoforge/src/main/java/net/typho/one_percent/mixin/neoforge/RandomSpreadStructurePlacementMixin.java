package net.typho.one_percent.mixin.neoforge;

import com.mojang.datafixers.util.Function8;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(RandomSpreadStructurePlacement.class)
public class RandomSpreadStructurePlacementMixin {
    @ModifyArg(
            method = "lambda$static$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/datafixers/Products$P8;apply(Lcom/mojang/datafixers/kinds/Applicative;Lcom/mojang/datafixers/util/Function8;)Lcom/mojang/datafixers/kinds/App;"
            ),
            remap = false,
            index = 1
    )
    @SuppressWarnings("unchecked")
    private static <T1, T2, T3, T4, T5, T6, T7, T8, R> Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> codec(Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> function) {
        return (t1, t2, t3, t4, t5, t6, t7, t8) -> {
            int spacing = ((Integer) t6) / 2;
            int separation = ((Integer) t7) / 2;

            if (spacing <= separation) {
                spacing = separation + 1;
            }

            return function.apply(t1, t2, t3, t4, t5, (T6) (Integer) spacing, (T7) (Integer) separation, t8);
        };
    }
}
