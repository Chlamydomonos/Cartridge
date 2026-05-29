package xyz.chlamydomonos.cartridge.mixinimpl.carryon

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import xyz.chlamydomonos.cartridge.hollow.HollowEntity
import xyz.chlamydomonos.cartridge.utils.carryingHollowUUID
import xyz.chlamydomonos.cartridge.utils.hollowCarriedByUUID

object PickupHandlerMixinImpl {
    fun injectTryPickupEntity(player: ServerPlayer, entity: Entity) {
        if (entity !is HollowEntity) {
            return
        }

        val hollowPlayer = entity.playerUUID?.let { entity.level().getPlayerByUUID(it) } ?: return
        hollowPlayer.hollowCarriedByUUID = player.uuid
        entity.playerUUID = null
        player.carryingHollowUUID = hollowPlayer.uuid
    }
}