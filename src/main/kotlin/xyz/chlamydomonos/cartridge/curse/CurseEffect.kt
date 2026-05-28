package xyz.chlamydomonos.cartridge.curse

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.neoforge.network.PacketDistributor
import xyz.chlamydomonos.cartridge.hollow.HollowEntity
import xyz.chlamydomonos.cartridge.loaders.EffectLoader
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.hollowUUID
import kotlin.math.ln

class CurseEffect : MobEffect(
    MobEffectCategory.HARMFUL,
    ColorUtil.rgb(0xf371ff)
) {
    companion object {
        fun apply(player: ServerPlayer, time: Int, level: Int) {
            if ((player.getEffect(EffectLoader.CURSE)?.amplifier ?: -1) >= level) {
                return
            }

            player.addEffect(MobEffectInstance(EffectLoader.CURSE, time * 100, level - 1))
        }

        fun turnToHollow(player: ServerPlayer) {
            val level = player.level()
            val hollow = HollowEntity.create(player)
            if (hollow != null) {
                player.dropAllDeathLoot(level, level.damageSources().source(DamageTypeLoader.CURSE))
                player.removeEffect(EffectLoader.CURSE)
            }
        }
    }

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
                    PacketDistributor.sendToPlayer(mob, IllusionPacket)
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
                    val level = mob.level()
                    if (level.random.nextBoolean()) {
                        mob.hurtServer(
                            level,
                            level.damageSources().source(DamageTypeLoader.CURSE_SIDE_EFFECT),
                            -ln(level.random.nextFloat()) * 4
                        )
                    }

                    PacketDistributor.sendToPlayer(mob, ConfusionPacket)
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
                        turnToHollow(mob)
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