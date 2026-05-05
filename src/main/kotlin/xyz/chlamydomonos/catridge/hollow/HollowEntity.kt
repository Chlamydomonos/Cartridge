package xyz.chlamydomonos.catridge.hollow

import com.geckolib.animatable.manager.AnimatableManager
import com.geckolib.constant.DefaultAnimations
import com.geckolib.util.GeckoLibUtil
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import net.minecraft.world.phys.AABB
import xyz.chlamydomonos.catridge.loaders.EntityDataLoader
import xyz.chlamydomonos.catridge.loaders.EntityLoader
import xyz.chlamydomonos.catridge.utils.hollowUUID
import java.util.*
import kotlin.jvm.optionals.getOrNull

class HollowEntity(type: EntityType<HollowEntity>, level: Level) : HollowEntityBase(type, level) {
    companion object {
        val PLAYER_UUID = SynchedEntityData.defineId(
            HollowEntity::class.java,
            EntityDataLoader.UUID
        )

        fun create(player: ServerPlayer, mount: Boolean = true): HollowEntity? {
            val level = player.level()
            val entity = EntityLoader.HOLLOW.create(level, EntitySpawnReason.EVENT) ?: return null
            entity.setPos(player.position())
            if (!level.addFreshEntity(entity)) {
                return null
            }

            entity.playerUUID = player.uuid
            player.hollowUUID = entity.uuid
            entity.customName = player.displayName
            entity.isCustomNameVisible = true

            if (!mount) {
                return entity
            }

            player.setCamera(entity)

            if (!player.startRiding(entity, true, true)) {
                return  null
            }

            player.boundingBox = AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

            return entity
        }
    }

    var playerUUID
        get() = entityData.get(PLAYER_UUID).getOrNull()
        set(value) { entityData.set(PLAYER_UUID, Optional.ofNullable(value)) }

    private val geoCache = GeckoLibUtil.createInstanceCache(this)

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        controllers.add(DefaultAnimations.genericWalkIdleController<HollowEntity>())
    }

    override fun getAnimatableInstanceCache() = geoCache

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, PanicGoal(this, 0.3))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 0.2))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 4f))
        goalSelector.addGoal(5, RandomLookAroundGoal(this))
    }

    override fun defineSynchedData(entityData: SynchedEntityData.Builder) {
        super.defineSynchedData(entityData)
        entityData.define(PLAYER_UUID, Optional.empty<UUID>())
    }

    override fun addAdditionalSaveData(output: ValueOutput) {
        super.addAdditionalSaveData(output)
        val uuid = entityData.get(PLAYER_UUID).getOrNull() ?: return
        output.putString("player_uuid", uuid.toString())
    }

    override fun readAdditionalSaveData(input: ValueInput) {
        super.readAdditionalSaveData(input)
        val uuid = input.getString("player_uuid").getOrNull() ?: return
        entityData.set(PLAYER_UUID, Optional.of(UUID.fromString(uuid)))
    }

    override fun tick() {
        super.tick()
        val level = level()
        val uuid = playerUUID
        if (level.isClientSide || uuid == null) {
            return
        }

        if (isDeadOrDying) {
            val player = level.getEntity(uuid) ?: return
            if (player !is ServerPlayer || player.isDeadOrDying) {
                return
            }

            val damageSource = lastDamageSource ?: level.damageSources().source(DamageTypes.GENERIC_KILL)
            player.hurtServer(level as ServerLevel, damageSource, Float.MAX_VALUE)
        }
    }
}