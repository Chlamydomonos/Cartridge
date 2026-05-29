package xyz.chlamydomonos.cartridge.mixinimpl.carryon

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import xyz.chlamydomonos.cartridge.utils.hollowCarriedBy

object CarriedObjectRenderMixinImpl {
    fun injectDrawEntity(player: Player, callback: CallbackInfo) {
        val self = Minecraft.getInstance().player ?: return
        if (player == self.hollowCarriedBy) {
            callback.cancel()
        }
    }
}