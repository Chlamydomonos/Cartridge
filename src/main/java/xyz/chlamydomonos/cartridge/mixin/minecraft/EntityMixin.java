package xyz.chlamydomonos.cartridge.mixin.minecraft;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.chlamydomonos.cartridge.mixinimpl.minecraft.EntityMixinImpl;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(
        method = "makeBoundingBox(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/AABB;",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void injectMakeBoundingBox(CallbackInfoReturnable<AABB> cir) {
        EntityMixinImpl.INSTANCE.injectMakeBoundingBox((Entity)(Object)this, cir);
    }
}
