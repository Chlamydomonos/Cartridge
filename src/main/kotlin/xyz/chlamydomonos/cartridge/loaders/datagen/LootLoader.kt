package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.core.registries.Registries
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BedPart
import net.minecraft.world.level.storage.loot.LootPool
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.entries.LootItem
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.GlobalLootModifierProvider
import net.neoforged.neoforge.common.loot.AddTableLootModifier
import net.neoforged.neoforge.common.loot.LootTableIdCondition
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock
import xyz.chlamydomonos.cartridge.loaders.BlockLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber
object LootLoader {
    val ADD_SPARAGMOS = ResourceKey.create(
        Registries.LOOT_TABLE,
        RLUtil.of("extra_tables/add_sparagmos")
    )

    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider { output ->
            LootTableProvider(
                output,
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
                    ),
                    LootTableProvider.SubProviderEntry(
                        { _ ->
                            LootTableSubProvider { output ->
                                output.accept(
                                    ADD_SPARAGMOS,
                                    LootTable.lootTable().withPool(
                                        LootPool.lootPool().add(
                                            LootItem.lootTableItem { ItemLoader.SPARAGMOS }
                                                .`when`(
                                                    LootItemRandomChanceCondition.randomChance(
                                                        0.01f
                                                    )
                                                )
                                        )
                                    )
                                )
                            }
                        },
                        LootContextParamSets.CHEST
                    )
                ),
                event.lookupProvider
            )
        }

        event.createProvider { output ->
            object : GlobalLootModifierProvider(output, event.lookupProvider, Cartridge.ID) {
                override fun start() {
                    add(
                        "gen_sparagmos",
                        AddTableLootModifier(
                            arrayOf(
                                LootTableIdCondition.builder(
                                    Identifier.withDefaultNamespace("chests/ancient_city")
                                ).build()
                            ),
                            0,
                            ADD_SPARAGMOS
                        )
                    )
                }

            }
        }
    }
}