package xyz.chlamydomonos.cartridge.hollow

import net.minecraft.network.protocol.game.ClientboundSetCameraPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.TriState
import net.minecraft.world.entity.Entity
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityMountEvent
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.utils.*

@EventBusSubscriber
object HollowPlayerEventListener {
    @SubscribeEvent
    fun onPlayerLoggedOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        if (player.level().isClientSide) {
            return
        }

        if (player.hollowCarriedByUUID != null) {
            return
        }

        val hollowEntity = player.hollowEntity
        if (hollowEntity != null) {
            player.hollowPos = hollowEntity.position()
            hollowEntity.remove(Entity.RemovalReason.DISCARDED)
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (player.level().isClientSide || player.hollowUUID == null || player !is ServerPlayer) {
            return
        }

        if (player.hollowCarriedByUUID != null) {
            return
        }

        val hollowEntity = player.hollowEntity

        if (player.isDeadOrDying) {
            player.hollowUUID = null
            hollowEntity?.remove(Entity.RemovalReason.DISCARDED)
            return
        }

        if (hollowEntity == null) {
            val entity = HollowEntity.create(player)
            val pos = player.hollowPos
            if (entity != null && pos != null) {
                entity.setPos(pos)
                player.hollowPos = null
            }
            return
        }

        if (player.camera != hollowEntity || player.tickCount % 20 == 0) {
            player.setCamera(hollowEntity)
            player.connection.send(ClientboundSetCameraPacket(player.camera))
            player.startRiding(hollowEntity, true, true)
        }

        if (player.vehicle != hollowEntity) {
            hollowEntity.remove(Entity.RemovalReason.DISCARDED)
        }
    }

    @SubscribeEvent
    fun onEntityMount(event: EntityMountEvent) {
        if (!event.isDismounting) {
            return
        }

        val rider = event.entityMounting
        val riding = event.entityBeingMounted

        if (riding is CarriedHollowEntity) {
            event.isCanceled = true
            return
        }

        if (riding is HollowEntity && rider is ServerPlayer) {
            if (riding.isDeadOrDying || rider.isDeadOrDying) {
                return
            }
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        if (event.isEndConquered) {
            return
        }
        val player = event.entity
        if (player is ServerPlayer) {
            player.isDeadHollow = false
        }
    }

    @SubscribeEvent
    fun onItemEntityPickup(event: ItemEntityPickupEvent.Pre) {
        val player = event.player
        if (player is ServerPlayer && player.hollowUUID != null) {
            event.setCanPickup(TriState.FALSE)
        }
    }
}