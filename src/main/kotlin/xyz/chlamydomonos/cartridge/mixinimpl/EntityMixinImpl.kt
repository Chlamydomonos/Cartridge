package xyz.chlamydomonos.cartridge.mixinimpl

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB
import xyz.chlamydomonos.cartridge.utils.hollowEntity

object EntityMixinImpl {
    const val C = 10000.0

    fun injectMakeBoundingBox(self: Entity): AABB? {
        @Suppress("SENSELESS_COMPARISON")
        if (self is ServerPlayer && self.connection != null && self.hollowEntity != null) {
            return AABB(C, C, C, C, C, C)
        }

        return null
    }
}