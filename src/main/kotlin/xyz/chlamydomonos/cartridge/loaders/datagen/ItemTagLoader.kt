package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.core.HolderLookup
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.ItemTagsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import top.theillusivec4.curios.api.CuriosTags
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber
object ItemTagLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            object : ItemTagsProvider(it, event.lookupProvider, Cartridge.ID) {
                override fun addTags(registries: HolderLookup.Provider) {
                    getOrCreateRawBuilder(CuriosTags.BACK)
                        .addElement(RLUtil.of("backpack"))

                    getOrCreateRawBuilder(CuriosTags.BRACELET)
                        .addElement(RLUtil.of("sparagmos"))

                    getOrCreateRawBuilder(CuriosTags.HEAD)
                        .addElement(RLUtil.of("gangway"))
                }
            }
        }
    }
}