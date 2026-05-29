package xyz.chlamydomonos.cartridge.utils

import net.minecraft.resources.Identifier
import xyz.chlamydomonos.cartridge.Cartridge

object RLUtil {
    fun of(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(Cartridge.ID, name)
    }
}