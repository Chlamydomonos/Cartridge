package xyz.chlamydomonos.catridge.curse

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.neoforge.network.PacketDistributor
import xyz.chlamydomonos.catridge.hollow.HollowEntity
import xyz.chlamydomonos.catridge.loaders.EffectLoader
import xyz.chlamydomonos.catridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.catridge.utils.ColorUtil
import xyz.chlamydomonos.catridge.utils.RLUtil
import xyz.chlamydomonos.catridge.utils.hollowUUID

class CurseEffect : MobEffect(
    MobEffectCategory.HARMFUL,
    ColorUtil.rgbAsInt(0xf371ff)
) {
    init {
        addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            RLUtil.of("effect.curse"),
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        ) {
            when (it + 1) {
                1 -> -0.25
                2 -> -0.5
                else -> -0.75
            }
        }
    }

    override fun applyEffectTick(serverLevel: ServerLevel, mob: LivingEntity, amplification: Int): Boolean {
        if (mob is ServerPlayer) {
            if (mob.hollowUUID != null) {
                mob.removeEffect(EffectLoader.CURSE)
                return true
            }

            when (amplification + 1) {
                2 -> {
                    mob.causeFoodExhaustion(8f)
                    val packet = VomitPacket(mob.uuid)
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, packet)
                }
                3 -> {
                    mob.causeFoodExhaustion(2f)
                    PacketDistributor.sendToPlayer(mob, IllusionPacket.INSTANCE)
                }
                4 -> {
                    val level = mob.level()
                    mob.hurtServer(
                        level,
                        level.damageSources().source(DamageTypeLoader.CURSE),
                        if (mob.health >= 1.5f) 1.1f else 0.1f
                    )
                    mob.heal(0.1f)
                    val packet = BloodPacket(mob.uuid)
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, packet)
                }
                5 -> {
                    PacketDistributor.sendToPlayer(mob, ConfusionPacket.INSTANCE)
                }
                6 -> {
                    val packet = ExplosionPacket(mob.uuid)
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(mob, packet)
                    val level = mob.level()
                    if (level.random.nextBoolean()) {
                        mob.hurtServer(
                            level,
                            level.damageSources().source(DamageTypeLoader.CURSE),
                            Float.MAX_VALUE
                        )
                        mob.removeEffect(EffectLoader.CURSE)
                    } else {
                        val hollow = HollowEntity.create(mob)
                        if (hollow != null) {
                            mob.removeEffect(EffectLoader.CURSE)
                        }
                    }
                }
            }
        }
        return true
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplification: Int): Boolean {
        return when (amplification + 1) {
            2 -> tickCount % 300 == 0
            3 -> tickCount % 200 == 0
            4 -> tickCount % 10 == 0
            5 -> tickCount % 200 == 0
            6 -> true
            else -> false
        }
    }
}