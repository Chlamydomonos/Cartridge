package xyz.chlamydomonos.catridge.utils

import net.minecraft.resources.Identifier
import xyz.chlamydomonos.catridge.Catridge

object RLUtil {
    fun of(name: String): Identifier {
        return Identifier.fromNamespaceAndPath(Catridge.ID, name)
    }

    fun mc(name: String): Identifier {
        return Identifier.withDefaultNamespace(name)
    }
}