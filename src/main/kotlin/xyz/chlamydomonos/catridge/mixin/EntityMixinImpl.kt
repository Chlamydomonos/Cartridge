package xyz.chlamydomonos.catridge.mixin

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import xyz.chlamydomonos.catridge.utils.hollowEntity

object EntityMixinImpl {
    fun injectMakeBoundingBox(self: Entity, context: CallbackInfoReturnable<AABB>) {
        if (self is ServerPlayer && self.hollowEntity != null) {
            context.returnValue = AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }
    }
}