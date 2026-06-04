package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.ClipContext
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.CollisionContext
import net.neoforged.neoforge.network.PacketDistributor
import org.joml.Vector3f
import org.joml.Vector3fc
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.cartridge.utils.ServerTickHandler
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

object GangwayHandler {
    const val LIGHT_ATTACK_BEAM_COUNT = 10
    const val LIGHT_ATTACK_SPREAD_ANGLE = 30f
    const val LIGHT_ATTACK_MAX_LENGTH = 10f
    const val HEAVY_ATTACK_MAX_LENGTH = 50f
    const val LIGHT_ATTACK_DAMAGE = 3f
    const val HEAVY_ATTACK_DAMAGE = 40f
    const val BEAM_VISIBLE_RANGE = 50.0
    const val MAX_BEAM_STEPS = 100

    data class Beam(
        val start: Vector3fc,
        val end: Vector3fc
    ) {
        companion object {
            val codec = StreamCodec.composite(
                ByteBufCodecs.VECTOR3F,
                Beam::start,
                ByteBufCodecs.VECTOR3F,
                Beam::end,
                ::Beam
            )
        }
    }

    private data class TraceState(
        val origin: Vec3,
        val currentPos: Vec3,
        val currentDir: Vec3,
        val remainingLength: Double,
        val remainingSteps: Int
    )

    private data class TraceStepResult(
        val beam: Beam,
        val hitEntity: Entity? = null,
        val nextState: TraceState? = null
    )

    private fun toBeam(
        start: Vec3,
        end: Vec3
    ) = Beam(
        Vector3f(start.x.toFloat(), start.y.toFloat(), start.z.toFloat()),
        Vector3f(end.x.toFloat(), end.y.toFloat(), end.z.toFloat())
    )

    private fun traceNextBeam(
        level: ServerLevel,
        state: TraceState
    ): TraceStepResult? {
        if (state.remainingLength <= 0.0 || state.remainingSteps <= 0) {
            return null
        }

        val endPos = state.currentPos.add(state.currentDir.scale(state.remainingLength))
        val blockHit = level.clip(
            ClipContext(
                state.currentPos,
                endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
            )
        )
        val blockHitPos = if (blockHit.type == HitResult.Type.MISS) endPos else blockHit.location
        val searchBox = AABB(state.currentPos, blockHitPos).inflate(1.0)

        var closestEntity: Entity? = null
        var closestHitPos: Vec3? = null
        var closestDist = Double.MAX_VALUE

        for (entity in level.getEntities(null, searchBox) { true }) {
            val hitbox = entity.boundingBox.inflate(entity.pickRadius.toDouble())
            if (hitbox.contains(state.origin)) {
                continue
            }

            val entityHit = hitbox.clip(state.currentPos, blockHitPos)
            if (entityHit.isPresent) {
                val hitPos = entityHit.get()
                val dist = state.currentPos.distanceToSqr(hitPos)
                if (dist > 0.0001 && dist < closestDist) {
                    closestDist = dist
                    closestEntity = entity
                    closestHitPos = hitPos
                }
            }
        }

        if (closestEntity != null && closestHitPos != null) {
            return TraceStepResult(
                beam = toBeam(state.currentPos, closestHitPos),
                hitEntity = closestEntity
            )
        }

        val beam = toBeam(state.currentPos, blockHitPos)
        if (blockHit.type != HitResult.Type.BLOCK) {
            return TraceStepResult(beam = beam)
        }

        val normal = Vec3(
            blockHit.direction.stepX.toDouble(),
            blockHit.direction.stepY.toDouble(),
            blockHit.direction.stepZ.toDouble()
        )
        val traveled = state.currentPos.distanceTo(blockHitPos)
        val nextRemainingLength = state.remainingLength - traveled
        if (nextRemainingLength <= 0.0) {
            return TraceStepResult(beam = beam)
        }

        val dot = state.currentDir.dot(normal)
        return TraceStepResult(
            beam = beam,
            nextState = TraceState(
                origin = state.origin,
                currentPos = blockHitPos.add(normal.scale(0.001)),
                currentDir = state.currentDir.subtract(normal.scale(2.0 * dot)).normalize(),
                remainingLength = nextRemainingLength,
                remainingSteps = state.remainingSteps - 1
            )
        )
    }

    private fun emitBeam(
        level: ServerLevel,
        beam: Beam,
        damage: Float
    ) {
        level.playSound(
            null,
            beam.start.x().toDouble(),
            beam.start.y().toDouble(),
            beam.start.z().toDouble(),
            SoundEvents.PLAYER_HURT_ON_FIRE,
            SoundSource.PLAYERS,
            damage / HEAVY_ATTACK_DAMAGE,
            5f
        )

        PacketDistributor.sendToPlayersNear(
            level,
            null,
            beam.start.x().toDouble(),
            beam.start.y().toDouble(),
            beam.start.z().toDouble(),
            BEAM_VISIBLE_RANGE,
            GangwayRenderPacket(beam)
        )
    }

    private fun continueAttack(
        level: ServerLevel,
        player: ServerPlayer,
        state: TraceState,
        damage: Float
    ) {
        val step = traceNextBeam(level, state) ?: return
        emitBeam(level, step.beam, damage)

        val hitEntity = step.hitEntity
        if (hitEntity != null) {
            if (hitEntity != player) {
                hitEntity.hurtServer(
                    level,
                    level.damageSources().source(DamageTypeLoader.GANGWAY, player),
                    damage
                )
            }
            return
        }

        val nextState = step.nextState ?: return
        ServerTickHandler.addTask(1) {
            continueAttack(level, player, nextState, damage)
        }
    }

    fun lightAttack(player: ServerPlayer, pos: Vector3fc, viewVector: Vector3fc) {
        val directions = mutableListOf<Vector3fc>()
        val random = player.random
        val baseDirection = Vector3f(viewVector).normalize()
        val helperAxis = if (abs(baseDirection.y()) < 0.999f) {
            Vector3f(0f, 1f, 0f)
        } else {
            Vector3f(1f, 0f, 0f)
        }
        val tangent = helperAxis.cross(baseDirection, Vector3f()).normalize()
        val bitangent = Vector3f(baseDirection).cross(tangent).normalize()
        val maxAngleRad = Math.toRadians(LIGHT_ATTACK_SPREAD_ANGLE.toDouble())

        repeat(LIGHT_ATTACK_BEAM_COUNT) {
            val phi = random.nextDouble() * 2.0 * PI
            val cosTheta = 1.0 - random.nextDouble() * (1.0 - cos(maxAngleRad))
            val sinTheta = sin(kotlin.math.acos(cosTheta))

            val direction = Vector3f(baseDirection)
                .mul(cosTheta.toFloat())
                .add(Vector3f(tangent).mul((sinTheta * cos(phi)).toFloat()))
                .add(Vector3f(bitangent).mul((sinTheta * sin(phi)).toFloat()))
                .normalize()

            directions.add(direction)
        }

        handleAttack(player, pos, directions, LIGHT_ATTACK_MAX_LENGTH, LIGHT_ATTACK_DAMAGE)
    }

    fun heavyAttack(player: ServerPlayer, pos: Vector3fc, viewVector: Vector3fc) {
        handleAttack(
            player,
            pos,
            listOf(Vector3f(viewVector)),
            HEAVY_ATTACK_MAX_LENGTH, HEAVY_ATTACK_DAMAGE
        )
    }

    fun handleAttack(
        player: ServerPlayer,
        pos: Vector3fc,
        directions: List<Vector3fc>,
        maxLength: Float,
        damage: Float
    ) {
        val level = player.level()
        val origin = Vec3(pos.x().toDouble(), pos.y().toDouble(), pos.z().toDouble())

        for (direction in directions) {
            val normalizedDirection = Vec3(
                direction.x().toDouble(),
                direction.y().toDouble(),
                direction.z().toDouble()
            ).normalize()

            val state = TraceState(
                origin = origin,
                currentPos = origin,
                currentDir = normalizedDirection,
                remainingLength = maxLength.toDouble(),
                remainingSteps = MAX_BEAM_STEPS
            )

            ServerTickHandler.addTask(0) {
                continueAttack(level, player, state, damage)
            }
        }
    }
}
