package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.abyss.AbyssEditToolItem
import xyz.chlamydomonos.cartridge.cartridge.BackpackItem
import xyz.chlamydomonos.cartridge.cartridge.CartridgeItem
import xyz.chlamydomonos.cartridge.utils.RLUtil

object ItemLoader {
    private val registry = DeferredRegister.createItems(Cartridge.ID)
    private val creativeModeTabRegistry = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Cartridge.ID)
    private val itemsInTab = mutableListOf<DeferredItem<out Item>>()
    @Suppress("unused")
    val TAB = creativeModeTabRegistry.register("cartridge") { ->
        CreativeModeTab.builder()
            .title(Component.translatable("tab.${Cartridge.ID}"))
            .displayItems { _, output ->
                for (holder in itemsInTab) {
                    output.accept(holder)
                }
            }
            .icon { ItemStack(CARTRIDGE) }
            .build()
    }

    fun <T : Item> register(name: String, factory: (id: ResourceKey<Item>) -> T): DeferredItem<T> {
        val holder = registry.register(name) { ->
            factory(ResourceKey.create(Registries.ITEM, RLUtil.of(name)))
        }
        itemsInTab.add(holder)
        return holder
    }

    val ABYSS_CREATE_1 by register("abyss_create_1") { AbyssEditToolItem(it, 1, AbyssEditToolItem.Operation.ADD) }
    val ABYSS_REMOVE_1 by register("abyss_remove_1") { AbyssEditToolItem(it, 1, AbyssEditToolItem.Operation.REMOVE) }
    val ABYSS_CREATE_2 by register("abyss_create_2") { AbyssEditToolItem(it, 2, AbyssEditToolItem.Operation.ADD) }
    val ABYSS_REMOVE_2 by register("abyss_remove_2") { AbyssEditToolItem(it, 2, AbyssEditToolItem.Operation.REMOVE) }
    val ABYSS_CREATE_3 by register("abyss_create_3") { AbyssEditToolItem(it, 3, AbyssEditToolItem.Operation.ADD) }
    val ABYSS_REMOVE_3 by register("abyss_remove_3") { AbyssEditToolItem(it, 3, AbyssEditToolItem.Operation.REMOVE) }
    val ABYSS_CREATE_4 by register("abyss_create_4") { AbyssEditToolItem(it, 4, AbyssEditToolItem.Operation.ADD) }
    val ABYSS_REMOVE_4 by register("abyss_remove_4") { AbyssEditToolItem(it, 4, AbyssEditToolItem.Operation.REMOVE) }
    val ABYSS_CREATE_5 by register("abyss_create_5") { AbyssEditToolItem(it, 5, AbyssEditToolItem.Operation.ADD) }
    val ABYSS_REMOVE_5 by register("abyss_remove_5") { AbyssEditToolItem(it, 5, AbyssEditToolItem.Operation.REMOVE) }
    val ABYSS_CREATE_6 by register("abyss_create_6") { AbyssEditToolItem(it, 6, AbyssEditToolItem.Operation.ADD) }
    val ABYSS_REMOVE_6 by register("abyss_remove_6") { AbyssEditToolItem(it, 6, AbyssEditToolItem.Operation.REMOVE) }

    val CARTRIDGE by register("cartridge") { CartridgeItem(it) }
    val BACKPACK by register("backpack") { BackpackItem(it) }

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
        creativeModeTabRegistry.register(bus)
    }
}