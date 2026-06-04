package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.ModelProvider
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.world.item.Item
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock
import xyz.chlamydomonos.cartridge.loaders.ItemLoader

@EventBusSubscriber(value = [Dist.CLIENT])
object ModelLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            object : ModelProvider(it, Cartridge.ID) {
                override fun registerModels(blockModels: BlockModelGenerators, itemModels: ItemModelGenerators) {
                    fun simpleItem(item: Item) {
                        itemModels.generateFlatItem(item, ModelTemplates.FLAT_ITEM)
                    }

                    simpleItem(ItemLoader.ABYSS_CREATE_1)
                    simpleItem(ItemLoader.ABYSS_REMOVE_1)
                    simpleItem(ItemLoader.ABYSS_CREATE_2)
                    simpleItem(ItemLoader.ABYSS_REMOVE_2)
                    simpleItem(ItemLoader.ABYSS_CREATE_3)
                    simpleItem(ItemLoader.ABYSS_REMOVE_3)
                    simpleItem(ItemLoader.ABYSS_CREATE_4)
                    simpleItem(ItemLoader.ABYSS_REMOVE_4)
                    simpleItem(ItemLoader.ABYSS_CREATE_5)
                    simpleItem(ItemLoader.ABYSS_REMOVE_5)
                    simpleItem(ItemLoader.ABYSS_CREATE_6)
                    simpleItem(ItemLoader.ABYSS_REMOVE_6)

                    itemModels.declareCustomModelItem(ItemLoader.CARTRIDGE)
                    itemModels.declareCustomModelItem(ItemLoader.BACKPACK)
                    SurgeryTableBlock.genModel(blockModels)

                    simpleItem(ItemLoader.HOLLOW_RANDOMIZER)

                    itemModels.declareCustomModelItem(ItemLoader.SPARAGMOS)
                    itemModels.declareCustomModelItem(ItemLoader.GANGWAY)
                }
            }
        }
    }
}