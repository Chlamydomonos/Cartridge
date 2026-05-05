package xyz.chlamydomonos.catridge.utils

import net.minecraft.server.level.ServerLevel
import xyz.chlamydomonos.catridge.abyss.AbyssManager

val ServerLevel.abyssManager get() = dataStorage.computeIfAbsent(AbyssManager.type)