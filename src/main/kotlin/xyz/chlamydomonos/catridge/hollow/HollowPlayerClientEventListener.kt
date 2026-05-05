package xyz.chlamydomonos.catridge.hollow

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RenderPlayerEvent
import xyz.chlamydomonos.catridge.utils.hollowUUID
import xyz.chlamydomonos.catridge.utils.isDeadHollow

@EventBusSubscriber(value = [Dist.CLIENT])
object HollowPlayerClientEventListener {
    @SubscribeEvent
    fun onRenderPlayer(event: RenderPlayerEvent.Pre<*>) {
        val playerId = event.renderState.id
        val player = Minecraft.getInstance().level?.getEntity(playerId) ?: return
        if (player is Player && (player.hollowUUID != null || player.isDeadHollow)) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onMouseButton(event: InputEvent.MouseButton.Pre) {
        val player = Minecraft.getInstance().player ?: return
        if (player.hollowUUID != null && Minecraft.getInstance().screen == null) {
            event.isCanceled = true
        }
    }
}