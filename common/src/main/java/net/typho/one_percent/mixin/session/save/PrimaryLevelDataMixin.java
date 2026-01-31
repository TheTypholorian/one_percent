package net.typho.one_percent.mixin.session.save;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.typho.one_percent.session.Session;
import net.typho.one_percent.session.SessionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimaryLevelData.class)
public class PrimaryLevelDataMixin implements SessionStorage {
    @Unique
    private Session one_percent$session;

    @Inject(
            method = "setTagData",
            at = @At("TAIL")
    )
    private void setTagData(RegistryAccess registry, CompoundTag nbt, CompoundTag playerNBT, CallbackInfo ci) {
        if (one_percent$session != null) {
            nbt.put("OnePercentSession", Session.CODEC.encodeStart(NbtOps.INSTANCE, one_percent$session).getOrThrow());
        }
    }

    @ModifyReturnValue(
            method = "parse",
            at = @At("TAIL")
    )
    private static <T> PrimaryLevelData parse(
            PrimaryLevelData original,
            @Local(argsOnly = true) Dynamic<T> tag
    ) {
        DataResult<Dynamic<T>> session = tag.get("OnePercentSession").get();

        if (session.isSuccess()) {
            ((SessionStorage) original).one_percent$setSession(Session.CODEC.decode(session.getOrThrow()).getOrThrow().getFirst());
        }

        return original;
    }

    @Override
    public @Nullable Session one_percent$getSession() {
        return one_percent$session;
    }

    @Override
    public void one_percent$setSession(@Nullable Session session) {
        one_percent$session = session;
    }
}
