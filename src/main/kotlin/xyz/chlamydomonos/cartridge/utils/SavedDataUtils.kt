package xyz.chlamydomonos.cartridge.utils

import net.minecraft.server.level.ServerLevel
import xyz.chlamydomonos.cartridge.abyss.AbyssManager

val ServerLevel.abyssManager get() = dataStorage.computeIfAbsent(AbyssManager.type)