package xyz.chlamydomonos.cartridge.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "makeBoundingBox()Lnet/minecraft/world/phys/AABB;", at = @At("HEAD"), cancellable = true)
    protected void injectMakeBoundingBox(CallbackInfoReturnable<AABB> cir) {
        var self = (Entity)(Object)this;
        EntityMixinImpl.INSTANCE.injectMakeBoundingBox(self, cir);
    }
}
