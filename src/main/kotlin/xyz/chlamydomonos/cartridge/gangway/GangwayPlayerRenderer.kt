package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderPlayerEvent

@EventBusSubscriber(value = [Dist.CLIENT])
object GangwayPlayerRenderer {
    @SubscribeEvent
    fun onRenderPlayer(event: RenderPlayerEvent.Pre<*>) {
        val playerId = event.renderState.id
        val player = Minecraft.getInstance().level?.getEntity(playerId) ?: return
        if (player !is Player) {
            return
        }

        val hasGangway = GangwayInputHandler.hasGangway(player)
        if (hasGangway) {
            event.renderState.showHat = false
        }
    }
}