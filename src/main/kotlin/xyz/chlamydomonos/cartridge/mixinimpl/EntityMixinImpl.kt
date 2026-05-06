package xyz.chlamydomonos.cartridge.mixinimpl

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import xyz.chlamydomonos.cartridge.utils.hollowEntity

object EntityMixinImpl {
    fun injectMakeBoundingBox(self: Entity): AABB? {
        @Suppress("SENSELESS_COMPARISON")
        if (self is ServerPlayer && self.connection != null && self.hollowEntity != null) {
            return AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        return null
    }
}