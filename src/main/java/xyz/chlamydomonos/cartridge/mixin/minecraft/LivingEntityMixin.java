package xyz.chlamydomonos.cartridge.mixin.minecraft;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.chlamydomonos.cartridge.mixinimpl.minecraft.LivingEntityMixinImpl;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(
        method = "getBedOrientation",
        at = @At("HEAD"),
        cancellable = true
    )
    void injectGetBedOrientation(CallbackInfoReturnable<Direction> cir) {
        LivingEntityMixinImpl.INSTANCE.injectGetBedOrientation((LivingEntity)(Object)this, cir);
    }

    @Inject(
        method = "isInWall",
        at = @At("HEAD"),
        cancellable = true
    )
    void injectIsInWall(CallbackInfoReturnable<Boolean> cir) {
        LivingEntityMixinImpl.INSTANCE.injectIsInWall((LivingEntity)(Object)this, cir);
    }
}
