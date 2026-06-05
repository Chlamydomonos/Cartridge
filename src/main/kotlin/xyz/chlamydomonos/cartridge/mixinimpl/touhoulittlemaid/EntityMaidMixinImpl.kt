package xyz.chlamydomonos.cartridge.mixinimpl.touhoulittlemaid

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

object EntityMaidMixinImpl {
    fun injectStartSleeping(self: EntityMaid, ci: CallbackInfo) {
        if (self.surgeryTablePos != null) {
            ci.cancel()
        }
    }
}