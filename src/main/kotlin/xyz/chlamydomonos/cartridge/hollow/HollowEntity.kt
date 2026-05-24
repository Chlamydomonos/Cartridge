package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.animatable.manager.AnimatableManager
import com.geckolib.constant.DefaultAnimations
import com.geckolib.util.GeckoLibUtil
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.EntitySpawnReason
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import xyz.chlamydomonos.cartridge.loaders.DataAttachmentLoader
import xyz.chlamydomonos.cartridge.loaders.EntityLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.hollowUUID
import xyz.chlamydomonos.cartridge.utils.isDeadHollow
import java.util.*
import kotlin.random.Random
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

class HollowEntity(type: EntityType<HollowEntity>, level: Level) : HollowEntityBase(type, level) {
    class DataDelegate<T>(val instance: HollowEntity, val field: KMutableProperty1<HollowEntityData, T>) {
        operator fun getValue(thisRef: Any?, propertyKey: KProperty<*>): T {
            return field.get(instance.data)
        }

        operator fun setValue(thisRef: Any?, propertyKey: KProperty<*>, value: T) {
            field.set(instance.data, value)
            instance.syncData(DataAttachmentLoader.HOLLOW_ENTITY_DATA)
        }
    }

    companion object {
        fun genData(uuid: UUID, includeUUID: Boolean = true): HollowEntityData {
            val random = Random(uuid.hashCode())

            var leftArmType = HollowEntityData.ArmType.NONE
            var rightArmType = HollowEntityData.ArmType.NONE
            when (random.nextInt(20)) {
                in 0..3 -> {}
                in 4..7 -> {
                    if (random.nextBoolean()) {
                        leftArmType = HollowEntityData.ArmType.entries[random.nextInt(1, 3)]
                    } else {
                        rightArmType = HollowEntityData.ArmType.entries[random.nextInt(1, 3)]
                    }
                }
                in 8..14 -> {
                    leftArmType = HollowEntityData.ArmType.entries[random.nextInt(1, 3)]
                    rightArmType = HollowEntityData.ArmType.entries[random.nextInt(1, 3)]
                }
                in 15..16 -> {
                    if (random.nextBoolean()) {
                        leftArmType = HollowEntityData.ArmType.LONG
                    } else {
                        rightArmType = HollowEntityData.ArmType.LONG
                    }
                }
                in 17..18 -> {
                    if (random.nextBoolean()) {
                        leftArmType = HollowEntityData.ArmType.entries[random.nextInt(1, 3)]
                        rightArmType = HollowEntityData.ArmType.LONG
                    } else {
                        leftArmType = HollowEntityData.ArmType.LONG
                        rightArmType = HollowEntityData.ArmType.entries[random.nextInt(1, 3)]
                    }
                }
                else -> {
                    leftArmType = HollowEntityData.ArmType.LONG
                    rightArmType = HollowEntityData.ArmType.LONG
                }
            }

            val headOffset = if (leftArmType == HollowEntityData.ArmType.NONE && rightArmType == HollowEntityData.ArmType.NONE) {
                HollowEntityData.Offset.entries[random.nextInt(3)]
            } else if (leftArmType == HollowEntityData.ArmType.NONE) {
                HollowEntityData.Offset.entries[random.nextInt(2)]
            } else if (rightArmType == HollowEntityData.ArmType.NONE) {
                HollowEntityData.Offset.entries[random.nextBoolean().let { if (it) 2 else 0 }]
            } else {
                HollowEntityData.Offset.MIDDLE
            }

            val hasLeftLeg = random.nextBoolean()
            val hasRightLeg = random.nextBoolean()
            val tailOffset = if (!hasLeftLeg && !hasRightLeg) {
                HollowEntityData.Offset.entries[random.nextInt(3)]
            } else if (!hasLeftLeg) {
                HollowEntityData.Offset.entries[random.nextInt(2)]
            } else if (!hasRightLeg) {
                HollowEntityData.Offset.entries[random.nextBoolean().let { if (it) 2 else 0 }]
            } else {
                HollowEntityData.Offset.MIDDLE
            }

            var leftEyeType = HollowEntityData.EyeType.NONE
            var rightEyeType = HollowEntityData.EyeType.NONE
            when (random.nextInt(20)) {
                0 -> {}
                in 1..8 -> {
                    val type = if (random.nextBoolean()) HollowEntityData.EyeType.SMALL else HollowEntityData.EyeType.DOT
                    if (random.nextBoolean()) {
                        leftEyeType = type
                    } else {
                        rightEyeType = type
                    }
                }
                in 9..16 -> {
                    val type = if (random.nextBoolean()) HollowEntityData.EyeType.SMALL else HollowEntityData.EyeType.DOT
                    leftEyeType = type
                    rightEyeType = type
                }
                else -> {
                    if (random.nextBoolean()) {
                        leftEyeType = HollowEntityData.EyeType.LARGE
                    } else {
                        rightEyeType = HollowEntityData.EyeType.LARGE
                    }
                }
            }

            return HollowEntityData(
                if (includeUUID) uuid else null,
                Mth.hsvToRgb(
                    random.nextFloat(),
                    HollowEntityData.DEFAULT_SATURATION,
                    HollowEntityData.DEFAULT_BRIGHTNESS
                ),
                HollowEntityData.BodyLength.random(random),
                leftArmType,
                rightArmType,
                headOffset,
                tailOffset,
                hasLeftLeg,
                hasRightLeg,
                random.nextBoolean(),
                HollowEntityData.EarType.random(random),
                HollowEntityData.EarType.random(random),
                leftEyeType,
                rightEyeType,
                HollowEntityData.MouthType.random(random)
            )
        }

        fun create(player: ServerPlayer, mount: Boolean = true): HollowEntity? {
            val level = player.level()
            val entity = EntityLoader.HOLLOW.create(level, EntitySpawnReason.EVENT) ?: return null
            entity.setPos(player.position())
            if (!level.addFreshEntity(entity)) {
                return null
            }

            entity.data = genData(player.uuid)
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
            player.abilities.invulnerable = true
            player.onUpdateAbilities()

            return entity
        }
    }

    var data
        get() = getData(DataAttachmentLoader.HOLLOW_ENTITY_DATA)
        set(value) {
            setData(DataAttachmentLoader.HOLLOW_ENTITY_DATA, value)
        }

    fun <T> forData(field: KMutableProperty1<HollowEntityData, T>) = DataDelegate(this, field)
    var playerUUID by forData(HollowEntityData::playerUUID)
    var color by forData(HollowEntityData::color)
    var bodyLength by forData(HollowEntityData::bodyLength)
    var leftArmType by forData(HollowEntityData::leftArmType)
    var rightArmType by forData(HollowEntityData::rightArmType)
    var headOffset by forData(HollowEntityData::headOffset)
    var tailOffset by forData(HollowEntityData::tailOffset)
    var hasLeftLeg by forData(HollowEntityData::hasLeftLeg)
    var hasRightLeg by forData(HollowEntityData::hasRightLeg)
    var hasLargeTail by forData(HollowEntityData::hasLargeTail)
    var leftEarType by forData(HollowEntityData::leftEarType)
    var rightEarType by forData(HollowEntityData::rightEarType)
    var leftEyeType by forData(HollowEntityData::leftEyeType)
    var rightEyeType by forData(HollowEntityData::rightEyeType)
    var mouthType by forData(HollowEntityData::mouthType)

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
            player.abilities.invulnerable = false
            player.onUpdateAbilities()
            player.hurtServer(level as ServerLevel, damageSource, Float.MAX_VALUE)
            if (player.isDeadOrDying) {
                player.isDeadHollow = true
            }
        }
    }

    override fun interact(player: Player, hand: InteractionHand, location: Vec3): InteractionResult {
        if (level().isClientSide || playerUUID != null) {
            return super.interact(player, hand, location)
        }

        val item = player.getItemInHand(hand)
        if (!item.`is`(ItemLoader.HOLLOW_RANDOMIZER)) {
            return super.interact(player, hand, location)
        }

        data = genData(UUID.randomUUID(), false)
        return InteractionResult.SUCCESS
    }
}