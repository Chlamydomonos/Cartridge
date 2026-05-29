package xyz.chlamydomonos.cartridge.mixinimpl.carryon

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import xyz.chlamydomonos.cartridge.hollow.HollowEntity
import xyz.chlamydomonos.cartridge.utils.carryingHollow
import xyz.chlamydomonos.cartridge.utils.hollowCarriedByUUID

object PlacementHandlerMixinImpl {
    private fun handleEntityPlacement(player: ServerPlayer, entity: Entity) {
        val carrying = player.carryingHollow
        if (carrying !is ServerPlayer) {
            return
        }

        carrying.hollowCarriedByUUID = null

        if (entity is HollowEntity) {
            entity.bind(carrying)
        } else {
            HollowEntity.create(carrying)
        }
    }

    fun injectTryPlaceEntity(player: ServerPlayer, entity: Entity) {
        handleEntityPlacement(player, entity)
    }

    fun injectTryStackEntity(player: ServerPlayer, entity: Entity) {
        handleEntityPlacement(player, entity)
    }
}