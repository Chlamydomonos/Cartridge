package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.world.item.Item
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.loaders.ItemLoader

@EventBusSubscriber
object ItemModelLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            object : ModelProvider(it, Cartridge.ID) {
                override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
                    fun simple(item: Item) {
                        itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM)
                    }

                    simple(ItemLoader.ABYSS_CREATE_1)
                    simple(ItemLoader.ABYSS_REMOVE_1)
                    simple(ItemLoader.ABYSS_CREATE_2)
                    simple(ItemLoader.ABYSS_REMOVE_2)
                    simple(ItemLoader.ABYSS_CREATE_3)
                    simple(ItemLoader.ABYSS_REMOVE_3)
                    simple(ItemLoader.ABYSS_CREATE_4)
                    simple(ItemLoader.ABYSS_REMOVE_4)
                    simple(ItemLoader.ABYSS_CREATE_5)
                    simple(ItemLoader.ABYSS_REMOVE_5)
                    simple(ItemLoader.ABYSS_CREATE_6)
                    simple(ItemLoader.ABYSS_REMOVE_6)
                }
            }
        }
    }
}