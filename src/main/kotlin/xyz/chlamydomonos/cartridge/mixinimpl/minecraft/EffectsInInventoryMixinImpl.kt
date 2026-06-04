package xyz.chlamydomonos.cartridge.mixinimpl.minecraft

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import xyz.chlamydomonos.cartridge.loaders.EffectLoader

object EffectsInInventoryMixinImpl {
    fun injectGetEffectName(effect: MobEffectInstance, cir: CallbackInfoReturnable<Component>) {
        if (effect.effect.value() == EffectLoader.BLESSING.value()) {
            val name = effect.effect.value().displayName.copy()
            cir.returnValue = name.append(Component.literal(" Lv.${effect.amplifier + 1}"))
        }
    }
}