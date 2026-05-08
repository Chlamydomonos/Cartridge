package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderPlayerEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.utils.ScreenOpenWrapper

@EventBusSubscriber(value = [Dist.CLIENT])
object CartridgePlayerClientEventListener {
    var isCartridge: Boolean? = null
    var requestPacketSent = false
    var hasScreen = false

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (!(player.level().isClientSide)) {
            return
        }

        if (isCartridge == null && !requestPacketSent) {
            ClientPacketDistributor.sendToServer(IsCartridgeRequestPacket)
            requestPacketSent = true
        }

        if (Minecraft.getInstance().screen == null) {
            if (hasScreen) {
                hasScreen = false
                isCartridge = null
            } else if (isCartridge == true) {
                ScreenOpenWrapper.openCartridgeScreen()
            }
        } else {
            hasScreen = true
        }
    }

    @SubscribeEvent
    fun onRenderPlayer(event: RenderPlayerEvent.Pre<*>) {
        val playerId = event.renderState.id
        val player = Minecraft.getInstance().level?.getEntity(playerId) ?: return
        if (player is Player && player.vehicle is CartridgeEntity) {
            event.isCanceled = true
        }
    }
}