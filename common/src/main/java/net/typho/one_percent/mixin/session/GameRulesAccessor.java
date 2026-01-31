package net.typho.one_percent.mixin.session;

import net.minecraft.world.level.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRules.class)
public interface GameRulesAccessor {
    @Invoker("register")
    static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        throw new AssertionError();
    }

    @Mixin(GameRules.IntegerValue.class)
    interface IntegerValue {
        @Invoker("create")
        static GameRules.Type<GameRules.IntegerValue> create(int defaultValue) {
            throw new AssertionError();
        }
    }
}
