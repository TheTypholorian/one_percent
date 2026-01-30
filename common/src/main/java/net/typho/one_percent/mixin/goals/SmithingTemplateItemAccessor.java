package net.typho.one_percent.mixin.goals;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.SmithingTemplateItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SmithingTemplateItem.class)
public interface SmithingTemplateItemAccessor {
    @Accessor("upgradeDescription")
    Component getUpgradeDescription();
}
