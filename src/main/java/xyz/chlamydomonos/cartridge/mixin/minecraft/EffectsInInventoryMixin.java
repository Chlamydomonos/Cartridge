package xyz.chlamydomonos.cartridge.mixin.minecraft;

import net.minecraft.client.gui.screens.inventory.EffectsInInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.chlamydomonos.cartridge.mixinimpl.minecraft.EffectsInInventoryMixinImpl;

@Mixin(EffectsInInventory.class)
public abstract class EffectsInInventoryMixin {
    @Inject(
        method = "getEffectName",
        at = @At("HEAD"),
        cancellable = true
    )
    void injectGetEffectName(MobEffectInstance effect, CallbackInfoReturnable<Component> cir) {
        EffectsInInventoryMixinImpl.INSTANCE.injectGetEffectName(effect, cir);
    }
}
