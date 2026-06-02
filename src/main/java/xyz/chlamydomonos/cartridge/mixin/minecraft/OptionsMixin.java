package xyz.chlamydomonos.cartridge.mixin.minecraft;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.chlamydomonos.cartridge.combat.CombatModeInputBlocker;

@Mixin(Options.class)
public abstract class OptionsMixin {
    @Inject(
        method = "save",
        at = @At("RETURN")
    )
    void injectSave(CallbackInfo ci) {
        CombatModeInputBlocker.INSTANCE.onUpdateKeyMappings();
    }
}
