package xyz.chlamydomonos.cartridge.utils

import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel

private val key = ResourceKey.create(Registries.DIMENSION, RLUtil.of("cartridge"))

val ServerLevel.cartridgeDimension: ServerLevel get() {
    val dimensionHolder = holderLookup(Registries.DIMENSION)
    val dimension = dimensionHolder.getOrThrow(key).value()
    if (dimension !is ServerLevel) {
        throw RuntimeException("WTF")
    }
    return dimension
}

val ServerLevel.isCartridgeDimension get() = dimension() == key