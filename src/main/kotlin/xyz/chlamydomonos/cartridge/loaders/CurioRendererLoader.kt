package xyz.chlamydomonos.cartridge.loaders

import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import top.theillusivec4.curios.api.client.ICurioRenderer
import xyz.chlamydomonos.cartridge.cartridge.BackpackRenderer

@EventBusSubscriber(value = [Dist.CLIENT])
object CurioRendererLoader {
    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            ICurioRenderer.register(ItemLoader.BACKPACK, ::BackpackRenderer)
        }
    }
}