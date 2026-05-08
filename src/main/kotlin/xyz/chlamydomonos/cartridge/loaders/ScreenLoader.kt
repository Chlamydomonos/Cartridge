package xyz.chlamydomonos.cartridge.loaders

import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import xyz.chlamydomonos.cartridge.cartridge.BackpackScreen
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableScreen

@EventBusSubscriber(value = [Dist.CLIENT])
object ScreenLoader {
    @SubscribeEvent
    fun onRegisterScreens(event: RegisterMenuScreensEvent) {
        event.register(MenuLoader.SURGERY_TABLE, ::SurgeryTableScreen)
        event.register(MenuLoader.BACKPACK, ::BackpackScreen)
    }
}