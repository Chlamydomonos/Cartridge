package xyz.chlamydomonos.cartridge.sparagmos

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import org.joml.Vector3f
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object SparagmosHandler {
    const val LENGTH = 30f

    fun calculateViewVector(xRot: Float, yRot: Float): Vector3f {
        val realXRot = xRot * (PI / 180f).toFloat()
        val realYRot = -yRot * (PI / 180f).toFloat()
        val yCos = cos(realYRot)
        val ySin = sin(realYRot)
        val xCos = cos(realXRot)
        val xSin = sin(realXRot)
        return Vector3f(ySin * xCos, -xSin, yCos * xCos)
    }

    fun getHitPositions(
        player: ServerPlayer,
        pos: Vector3f,
        pitch: Float,
        yaw: Float,
    ): MutableList<Vec3> {
        val viewVector = calculateViewVector(pitch, yaw)
        val handPos = pos
        val targetPos = Vector3f()
        handPos.add(viewVector.normalize(LENGTH), targetPos)
        val handPosD = Vec3(handPos.x.toDouble(), handPos.y.toDouble(), handPos.z.toDouble())
        val targetPosD = Vec3(targetPos.x.toDouble(), targetPos.y.toDouble(), targetPos.z.toDouble())

        val level = player.level()
        val result = mutableListOf(targetPosD)
        BlockGetter.traverseBlocks(
            handPosD,
            targetPosD,
            level,
            { level, pos ->
                if (!(level.getBlockState(pos).isAir)) {
                    result.add(pos.center)
                }
                null
            }
        ) { true }

        val aabb = AABB(handPosD, targetPosD).inflate(3.0)
        level.entities.get(aabb) {
            if (it.isSpectator || !it.isPickable || it == player) {
                return@get
            }

            val box = it.boundingBox.inflate(it.pickRadius.toDouble())

            if (box.clip(handPosD, targetPosD).isPresent || box.contains(handPosD)) {
                result.add(it.position().add(0.0, (it.bbHeight / 2).toDouble(), 0.0))
            }
        }

        return result
    }

    @Suppress("Unused")
    fun canDestroyBlock(player: ServerPlayer, level: BlockGetter, pos: BlockPos): Boolean {
        return player.abilities.mayBuild
    }

    fun handle(
        player: ServerPlayer,
        pos: Vector3f,
        pitch: Float,
        yaw: Float,
    ) {
        val hitPositions = getHitPositions(player, pos, pitch, yaw)
        val level = player.level()

        level.playSound(
            null,
            player,
            SoundEvents.GENERIC_EXPLODE.value(),
            SoundSource.PLAYERS,
            1f,
            1f
        )

        for (hitPos in hitPositions) {
            level.explode(
                player,
                level.damageSources().source(DamageTypeLoader.SPARAGMOS, player),
                object : ExplosionDamageCalculator() {
                    override fun shouldBlockExplode(
                        explosion: Explosion,
                        level: BlockGetter,
                        pos: BlockPos,
                        state: BlockState,
                        power: Float
                    ): Boolean {
                        return canDestroyBlock(player, level, pos)
                    }

                    override fun getEntityDamageAmount(explosion: Explosion, entity: Entity, exposure: Float): Float {
                        if (entity == player) {
                            return 0f
                        }

                        val doubleRadius = explosion.radius() * 2.0f
                        val center = explosion.center()
                        val dist = sqrt(entity.distanceToSqr(center)) / doubleRadius
                        if (dist < 0.4) {
                            return Float.MAX_VALUE
                        }

                        val pow = (1.0 - dist) * exposure
                        return ((pow * pow + pow) / 2.0 * 14.0 * doubleRadius + 1.0).toFloat()
                    }
                },
                hitPos.x,
                hitPos.y,
                hitPos.z,
                2f,
                false,
                Level.ExplosionInteraction.BLOCK
            )
        }
        PacketDistributor.sendToPlayersNear(
            level,
            null,
            pos.x.toDouble(),
            pos.y.toDouble(),
            pos.z.toDouble(),
            LENGTH.toDouble(),
            SparagmosRenderPacket(pos, pitch, yaw)
        )
    }
}