package xyz.chlamydomonos.cartridge.loaders.datagen

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.criterion.DataComponentMatchers
import net.minecraft.advancements.criterion.ImpossibleTrigger
import net.minecraft.advancements.criterion.InventoryChangeTrigger
import net.minecraft.advancements.criterion.ItemPredicate
import net.minecraft.core.component.DataComponentExactPredicate
import net.minecraft.core.registries.Registries
import net.minecraft.data.advancements.AdvancementProvider
import net.minecraft.data.advancements.AdvancementSubProvider
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil
import java.util.*

@EventBusSubscriber
object AdvancementLoader {
    @SubscribeEvent
    fun onGatherData(event: GatherDataEvent.Client) {
        event.createProvider { packOutput, lp ->
            AdvancementProvider(
                packOutput,
                lp,
                mutableListOf(
                    AdvancementSubProvider { registries, output ->
                        val cartridge = Advancement.Builder.advancement()
                            .display(
                                ItemLoader.CARTRIDGE,
                                Component.translatable("advancement.cartridge.cartridge"),
                                Component.translatable("advancement.cartridge.cartridge.description"),
                                Identifier.withDefaultNamespace(
                                    "gui/advancements/backgrounds/stone"
                                ),
                                AdvancementType.TASK,
                                true,
                                true,
                                true
                            )
                            .addCriterion(
                                "has_cartridge",
                                InventoryChangeTrigger.TriggerInstance.hasItems(
                                    ItemPredicate.Builder.item()
                                        .of(
                                            registries.lookup(Registries.ITEM).get(),
                                            ItemLoader.CARTRIDGE
                                        )
                                        .withComponents(
                                            DataComponentMatchers.Builder.components()
                                                .exact(
                                                    DataComponentExactPredicate.builder()
                                                        .expect(
                                                            DataComponentLoader.OPTIONAL_NAME,
                                                            Optional.empty()
                                                        )
                                                        .build()
                                                )
                                                .build()
                                        )
                                )
                            )
                            .save(output, RLUtil.of("cartridge"))

                        Advancement.Builder.advancement()
                            .parent(cartridge)
                            .display(
                                ItemLoader.GANGWAY,
                                Component.translatable("advancement.cartridge.maid"),
                                Component.translatable("advancement.cartridge.maid.description"),
                                null,
                                AdvancementType.TASK,
                                true,
                                true,
                                true
                            )
                            .addCriterion(
                                "code_trigger",
                                CriteriaTriggers.IMPOSSIBLE.createCriterion(
                                    ImpossibleTrigger.TriggerInstance()
                                )
                            )
                            .save(output, RLUtil.of("maid"))
                    }
                )
            )
        }
    }
}