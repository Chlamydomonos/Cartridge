package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.TriState
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityMountEvent
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.loaders.EntityLoader
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.cartridge.utils.*

@EventBusSubscriber
object CartridgePlayerEventListener {
    fun <T : PlayerTrackEntity> trackPlayer(rider: ServerPlayer, target: ServerPlayer, entityType: EntityType<T>): T? {
        if (rider.level().dimension() != target.level().dimension()) {
            rider.teleportTo(
                target.level(),
                target.x,
                target.y,
                target.z,
                setOf(),
                rider.yRot,
                rider.xRot,
                false
            )
            return null
        }

        val level = target.level()
        val entity = entityType.create(level, EntitySpawnReason.EVENT) ?: return null
        entity.trackedPlayer = target
        if (!level.addFreshEntity(entity)) {
            Cartridge.logger.warn("Failed to add player track entity")
            return entity
        }

        rider.boundingBox = AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        rider.abilities.invulnerable = true
        rider.onUpdateAbilities()
        rider.setCamera(target)
        val rideSuccess = rider.startRiding(entity, true, true)
        if (!rideSuccess) {
            Cartridge.logger.warn("Ride failed")
        }
        return entity
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        val level = player.level()
        if (player.cartridgeStatus == CartridgeManager.CartridgeStatus.FREE) {
            if (!level.isCartridgeDimension) {
                player.teleportTo(
                    level.cartridgeDimension,
                    level.random.nextDouble() * 100,
                    100.0,
                    level.random.nextDouble() * 100,
                    setOf(),
                    player.yRot,
                    player.xRot,
                    false
                )
            }

            if (player.camera != player) {
                player.setCamera(null)
            }

            return
        }

        if (player.cartridgeStatus == CartridgeManager.CartridgeStatus.EQUIPPED) {
            val vehicle = player.vehicle
            if (vehicle is CartridgeEntity && vehicle.removalReason == null) {
                return
            }

            val equipper = player.equipper ?: return
            trackPlayer(player, equipper, EntityLoader.CARTRIDGE)
            return
        }

        if (player.cartridgeStatus == CartridgeManager.CartridgeStatus.DEAD) {
            if (player.isDeadOrDying) {
                player.cartridgeStatus = CartridgeManager.CartridgeStatus.NONE
                return
            }

            val hurt = player.hurtServer(
                level,
                level.damageSources().source(DamageTypeLoader.CARTRIDGE, player.equipper),
                Float.MAX_VALUE
            )
            if (hurt) {
                player.cartridgeStatus = CartridgeManager.CartridgeStatus.NONE
            }

            return
        }

        if (player.cartridgeStatus == CartridgeManager.CartridgeStatus.NONE) {
            if (level.isCartridgeDimension) {
                player.hurtServer(
                    level,
                    level.damageSources().source(DamageTypeLoader.CARTRIDGE, player.equipper),
                    Float.MAX_VALUE
                )
            }
        }
    }

    @SubscribeEvent
    fun onEntityMount(event: EntityMountEvent) {
        if (!event.isDismounting) {
            return
        }

        val riding = event.entityBeingMounted
        if (riding is CartridgeEntity && !riding.removingSelf) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onItemEntityPickup(event: ItemEntityPickupEvent.Pre) {
        val player = event.player
        if (player !is ServerPlayer) {
            return
        }

        if (player.isCartridge) {
            event.setCanPickup(TriState.FALSE)
        }
    }
}