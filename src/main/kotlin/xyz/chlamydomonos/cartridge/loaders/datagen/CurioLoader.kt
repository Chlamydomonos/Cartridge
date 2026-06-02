package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.world.entity.EntityType
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import top.theillusivec4.curios.api.CuriosDataProvider
import xyz.chlamydomonos.cartridge.Cartridge

@EventBusSubscriber
object CurioLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            object : CuriosDataProvider(Cartridge.ID, it, event.lookupProvider) {
                override fun generate(registries: HolderLookup.Provider) {
                    createEntities("player")
                        .addEntities(EntityType.PLAYER)
                        .addAllPresetSlots()

                    createSlot("hands")
                        .size(2)

                    createSlot("bracelet")
                        .size(2)
                }
            }
        }
    }
}