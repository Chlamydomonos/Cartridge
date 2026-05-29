package xyz.chlamydomonos.cartridge.loaders

import com.geckolib.renderer.base.GeoRenderState
import net.minecraft.client.renderer.entity.NoopRenderer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import xyz.chlamydomonos.cartridge.hollow.HollowRenderer

@EventBusSubscriber(value = [Dist.CLIENT])
object EntityRendererLoader {
    private abstract class TypeHelper : EntityRenderState(), GeoRenderState

    @SubscribeEvent
    fun onRegisterEntityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerEntityRenderer(EntityLoader.HOLLOW) {
            HollowRenderer<TypeHelper>(it, EntityLoader.HOLLOW)
        }
        event.registerEntityRenderer(EntityLoader.CARTRIDGE) { NoopRenderer(it) }
        event.registerEntityRenderer(EntityLoader.CARRIED_HOLLOW) { NoopRenderer(it) }
    }
}