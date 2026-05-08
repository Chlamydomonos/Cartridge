package xyz.chlamydomonos.cartridge.mixinimpl

import net.neoforged.fml.ModContainer
import net.neoforged.fml.i18n.FMLTranslations

object ModListWidgetModEntryMixinImpl {
    @Suppress("UnstableApiUsage")
    fun modifyArgsExtractContentStripControlCodes(arg: String, mod: ModContainer): String {
        return FMLTranslations.getPattern(
            "chlamydomonos.fml.menu.mods.info.name.${mod.modId}"
        ) { arg }
    }
}