package xyz.chlamydomonos.cartridge.blessing

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import xyz.chlamydomonos.cartridge.loaders.EffectLoader
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.RLUtil

class BlessingEffect : MobEffect(
    MobEffectCategory.BENEFICIAL,
    ColorUtil.rgb(0xf0f0f0)
) {
    companion object {
        fun add(player: ServerPlayer, level: Int) {
            val currentAmplifier = player.getEffect(EffectLoader.BLESSING)?.amplifier ?: -1
            val amplifier = currentAmplifier + level

            if (currentAmplifier >= 0) {
                player.removeEffect(EffectLoader.BLESSING)
            }

            player.addEffect(
                MobEffectInstance(
                    EffectLoader.BLESSING,
                    -1,
                    amplifier,
                    false,
                    false,
                    true
                )
            )
        }
    }

    init {
        addAttributeModifier(
            Attributes.ATTACK_DAMAGE,
            RLUtil.of("effect.blessing"),
            AttributeModifier.Operation.ADD_VALUE
        ) {
            (it + 1).toDouble()
        }

        addAttributeModifier(
            Attributes.ARMOR,
            RLUtil.of("effect.blessing"),
            AttributeModifier.Operation.ADD_VALUE
        ) {
            (it + 1).toDouble()
        }
    }
}