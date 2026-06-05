package xyz.chlamydomonos.cartridge.mixinimpl.touhoulittlemaid

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

object MaidClearSleepTaskMixinImpl {
    fun injectStart(maid: EntityMaid, ci: CallbackInfo) {
        if (maid.surgeryTablePos != null) {
            ci.cancel()
        }
    }
}