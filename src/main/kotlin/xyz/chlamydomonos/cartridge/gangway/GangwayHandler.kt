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

    fun makeBeams(
        level: ServerLevel,
        startPos: Vector3fc,
        direction: Vector3fc,
        maxLength: Float,
        output: MutableList<Beam>
    ): Entity? {
        val startVec3 = Vec3(startPos.x().toDouble(), startPos.y().toDouble(), startPos.z().toDouble())
        var currentPos = startVec3
        var currentDir = Vec3(direction.x().toDouble(), direction.y().toDouble(), direction.z().toDouble())
            .normalize()
        var remainingLength = maxLength.toDouble()

        repeat(100) {
            if (remainingLength <= 0.0) {
                return null
            }

            val endPos = currentPos.add(currentDir.scale(remainingLength))
            val blockHit = level.clip(
                ClipContext(
                    currentPos,
                    endPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    CollisionContext.empty()
                )
            )
            val blockHitPos = if (blockHit.type == HitResult.Type.MISS) endPos else blockHit.location
            val searchBox = AABB(currentPos, blockHitPos).inflate(1.0)

            var closestEntity: Entity? = null
            var closestHitPos: Vec3? = null
            var closestDist = Double.MAX_VALUE

            for (entity in level.getEntities(null, searchBox) { true }) {
                val hitbox = entity.boundingBox.inflate(entity.pickRadius.toDouble())
                if (hitbox.contains(startVec3)) {
                    continue
                }

                val entityHit = hitbox.clip(currentPos, blockHitPos)
                if (entityHit.isPresent) {
                    val hitPos = entityHit.get()
                    val dist = currentPos.distanceToSqr(hitPos)
                    if (dist > 0.0001 && dist < closestDist) {
                        closestDist = dist
                        closestEntity = entity
                        closestHitPos = hitPos
                    }
                }
            }

            if (closestEntity != null && closestHitPos != null) {
                output.add(
                    Beam(
                        Vector3f(currentPos.x.toFloat(), currentPos.y.toFloat(), currentPos.z.toFloat()),
                        Vector3f(closestHitPos.x.toFloat(), closestHitPos.y.toFloat(), closestHitPos.z.toFloat())
                    )
                )
                return closestEntity
            }

            output.add(
                Beam(
                    Vector3f(currentPos.x.toFloat(), currentPos.y.toFloat(), currentPos.z.toFloat()),
                    Vector3f(blockHitPos.x.toFloat(), blockHitPos.y.toFloat(), blockHitPos.z.toFloat())
                )
            )

            if (blockHit.type != HitResult.Type.BLOCK) {
                return null
            }

            val normal = Vec3(
                blockHit.direction.stepX.toDouble(),
                blockHit.direction.stepY.toDouble(),
                blockHit.direction.stepZ.toDouble()
            )
            val traveled = currentPos.distanceTo(blockHitPos)
            val dot = currentDir.dot(normal)
            currentDir = currentDir.subtract(normal.scale(2.0 * dot)).normalize()
            remainingLength -= traveled
            currentPos = blockHitPos.add(normal.scale(0.001))
        }

        return null
    }

    fun lightAttack(player: ServerPlayer, pos: Vector3fc, viewVector: Vector3fc) {
        val beams = mutableListOf<List<Beam>>()
        val entities = mutableListOf<Entity?>()
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

            val path = mutableListOf<Beam>()
            val entity = makeBeams(
                player.level(),
                pos,
                direction,
                LIGHT_ATTACK_MAX_LENGTH,
                path
            )
            beams.add(path)
            entities.add(entity)
        }

        handleAttack(player, beams, entities, LIGHT_ATTACK_DAMAGE)
    }

    fun heavyAttack(player: ServerPlayer, pos: Vector3fc, viewVector: Vector3fc) {
        val level = player.level()
        val beams = mutableListOf<Beam>()
        val entity = makeBeams(
            level,
            pos,
            viewVector,
            HEAVY_ATTACK_MAX_LENGTH,
            beams
        )

        handleAttack(player, listOf(beams), listOf(entity), HEAVY_ATTACK_DAMAGE)
    }

    fun handleAttack(
        player: ServerPlayer,
        beams: List<List<Beam>>,
        entities: List<Entity?>,
        damage: Float
    ) {
        val level = player.level()

        for ((pathIndex, path) in beams.withIndex()) {
            for ((beamIndex, beam) in path.withIndex()) {
                ServerTickHandler.addTask(beamIndex) {
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

                    if (beamIndex != path.lastIndex) {
                        return@addTask
                    }

                    val entity = entities.getOrNull(pathIndex) ?: return@addTask
                    if (entity == player) {
                        return@addTask
                    }

                    entity.hurtServer(
                        level,
                        level.damageSources().source(DamageTypeLoader.GANGWAY, player),
                        damage
                    )
                }
            }
        }
    }
}
