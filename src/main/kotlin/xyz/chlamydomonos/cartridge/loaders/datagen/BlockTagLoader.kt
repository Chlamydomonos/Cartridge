package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.BlockTagsProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber
object BlockTagLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            object : BlockTagsProvider(it, event.lookupProvider, Cartridge.ID) {
                override fun addTags(registries: HolderLookup.Provider) {
                    getOrCreateRawBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                        .addElement(RLUtil.of("surgery_table"))
                }
            }
        }
    }
}