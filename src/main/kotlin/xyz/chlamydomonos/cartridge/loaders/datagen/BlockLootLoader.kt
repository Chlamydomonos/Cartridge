package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock
import xyz.chlamydomonos.cartridge.loaders.BlockLoader

@EventBusSubscriber
object BlockLootLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider {
            LootTableProvider(
                it,
                setOf(),
                listOf(
                    LootTableProvider.SubProviderEntry(
                        { lp ->
                            object : BlockLootSubProvider(
                                setOf(),
                                FeatureFlags.DEFAULT_FLAGS,
                                lp
                            ) {
                                override fun generate() {
                                    add(BlockLoader.SURGERY_TABLE) { block ->
                                        createSinglePropConditionTable(
                                            block,
                                            SurgeryTableBlock.PART,
                                            BedPart.FOOT
                                        )
                                    }
                                }

                                override fun getKnownBlocks(): Iterable<Block> {
                                    return BlockLoader.blocks.map { it.get() }
                                }
                            }
                        },
                        LootContextParamSets.BLOCK
                    )
                ),
                event.lookupProvider
            )
        }
    }
}