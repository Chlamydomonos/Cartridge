package xyz.chlamydomonos.catridge.utils

import net.minecraft.resources.Identifier
import xyz.chlamydomonos.catridge.Cartridge

object RLUtil {
    fun of(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(Cartridge.ID, name)
    }
}