package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.type.capability.ICurioItem

@EventBusSubscriber
object CurioLoader {
    fun <T> registerCurioItem(item: T) where T : Item, T : ICurioItem {
        CuriosApi.registerCurio(item, item)
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        event.enqueueWork {
            registerCurioItem(ItemLoader.BACKPACK)
        }
    }
}