package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.utils.cartridgeStatus

abstract class PlayerTrackEntity(type: EntityType<*>, level: Level) : Entity(type, level) {
    var trackedPlayer: ServerPlayer? = null
    var removingSelf = false

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {}
    override fun hurtServer(level: ServerLevel, source: DamageSource, damage: Float) = false
    override fun readAdditionalSaveData(input: ValueInput) {}
    override fun addAdditionalSaveData(output: ValueOutput) {}

    fun removeSelf() {
        removingSelf = true
        unRide()
        remove(RemovalReason.DISCARDED)
    }

    override fun tick() {
        super.tick()
        if (level().isClientSide) {
            return
        }

        val trackedPlayer = this.trackedPlayer
        if (trackedPlayer == null || trackedPlayer.removalReason != null) {
            Cartridge.logger.debug("Removed: no tracked player")
            removeSelf()
            return
        }

        if (trackedPlayer.level().dimension() != level().dimension()) {
            Cartridge.logger.debug("Removed: not in same dimension")
            removeSelf()
            return
        }

        if (passengers.isEmpty()) {
            Cartridge.logger.debug("Removed: no passenger")
            removeSelf()
            return
        }

        val passenger = passengers[0]
        if (passenger !is ServerPlayer) {
            throw RuntimeException("Non-player passenger on player track entity")
        }
        val status = passenger.cartridgeStatus
        if (status != CartridgeManager.CartridgeStatus.EQUIPPED) {
            Cartridge.logger.debug("Removed: not equipped")
            removeSelf()
            return
        }

        setPos(trackedPlayer.position())
    }
}