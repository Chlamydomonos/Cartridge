package xyz.chlamydomonos.cartridge.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import java.util.*
import kotlin.jvm.optionals.getOrNull

var ItemStack.optionalBlockPos
    get() = get(DataComponentLoader.OPTIONAL_BLOCK_POS)?.getOrNull()
    set(value) { set(DataComponentLoader.OPTIONAL_BLOCK_POS, Optional.ofNullable(value)) }

var ItemStack.optionalUUID
    get() = get(DataComponentLoader.OPTIONAL_UUID)?.getOrNull()
    set(value) { set(DataComponentLoader.OPTIONAL_UUID, Optional.ofNullable(value)) }

var ItemStack.optionalName
    get() = get(DataComponentLoader.OPTIONAL_NAME)?.getOrNull()
    set(value) { set(DataComponentLoader.OPTIONAL_NAME, Optional.ofNullable(value)) }

var ItemStack.cartridgeDurability
    get() = get(DataComponentLoader.CARTRIDGE_DURABILITY) ?: -1
    set(value) { set(DataComponentLoader.CARTRIDGE_DURABILITY, value) }

var ItemStack.container
    get() = get(DataComponents.CONTAINER)
    set(value) { set(DataComponents.CONTAINER, value) }