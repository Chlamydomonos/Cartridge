package xyz.chlamydomonos.cartridge.mixinimpl

import net.neoforged.fml.ModContainer
import net.neoforged.fml.i18n.FMLTranslations
import net.neoforged.neoforgespi.language.IModInfo

object ModListScreenMixinImpl {
    @Suppress("UnstableApiUsage")
    fun modifyArgUpdateCacheLinesAdd(arg: String, selectedMod: IModInfo): String {
        return FMLTranslations.getPattern(
            "chlamydomonos.fml.menu.mods.info.name.${selectedMod.modId}"
        ) { arg }
    }

    @Suppress("UnstableApiUsage")
    fun modifyArgInit(arg: String, mod: ModContainer): String {
        return FMLTranslations.getPattern(
            "chlamydomonos.fml.menu.mods.info.name.${mod.modId}"
        ) { arg }
    }
}