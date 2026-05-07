package xyz.chlamydomonos.cartridge.mixinimpl

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import xyz.chlamydomonos.cartridge.utils.hollowEntity

object EntityMixinImpl {
    const val C = 10000.0

    fun injectMakeBoundingBox(self: Entity, context: CallbackInfoReturnable<AABB>) {
        @Suppress("SENSELESS_COMPARISON")
        if (self is ServerPlayer && self.connection != null && self.hollowEntity != null) {
            context.returnValue = AABB(C, C, C, C, C, C)
        }
    }
}