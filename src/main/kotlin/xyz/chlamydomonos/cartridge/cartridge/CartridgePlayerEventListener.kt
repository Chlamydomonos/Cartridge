package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.phys.AABB
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.EntityMountEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.loaders.EntityLoader
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.cartridge.utils.cartridgeDimension
import xyz.chlamydomonos.cartridge.utils.cartridgeStatus
import xyz.chlamydomonos.cartridge.utils.equipper
import xyz.chlamydomonos.cartridge.utils.isCartridgeDimension

@EventBusSubscriber
object CartridgePlayerEventListener {
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
            if (player.level().dimension() != equipper.level().dimension()) {
                player.teleportTo(
                    equipper.level(),
                    equipper.x,
                    equipper.y,
                    equipper.z,
                    setOf(),
                    player.yRot,
                    player.xRot,
                    false
                )
                return
            }

            val entity = EntityLoader.CARTRIDGE.create(equipper.level(), EntitySpawnReason.EVENT) ?: return
            entity.trackedPlayer = equipper
            if (!level.addFreshEntity(entity)) {
                return
            }

            player.boundingBox = AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
            player.abilities.invulnerable = true
            player.onUpdateAbilities()
            player.setCamera(equipper)
            val rideSuccess = player.startRiding(entity, true, true)
            if (!rideSuccess) {
                Cartridge.logger.warn("Ride failed")
            }
            Cartridge.logger.debug(
                "Spawned cartridge entity, passengers: {}, tracked: {}",
                entity.passengers.size,
                entity.trackedPlayer
            )
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
}