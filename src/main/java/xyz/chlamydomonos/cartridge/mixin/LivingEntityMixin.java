package xyz.chlamydomonos.cartridge.mixin;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.chlamydomonos.cartridge.mixinimpl.LivingEntityMixinImpl;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
            method = "getBedOrientation",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    void injectGetBedOrientation(CallbackInfoReturnable<Direction> cir) {
        var returnValue = LivingEntityMixinImpl.INSTANCE.injectGetBedOrientation((LivingEntity)(Object)this);
        if (returnValue != null) {
            cir.setReturnValue(returnValue);
        }
    }
}
