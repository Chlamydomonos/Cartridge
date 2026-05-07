package xyz.chlamydomonos.cartridge.utils

import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import xyz.chlamydomonos.cartridge.abyss.AbyssManager
import xyz.chlamydomonos.cartridge.cartridge.CartridgeManager

val ServerLevel.abyssManager get() = dataStorage.computeIfAbsent(AbyssManager.type)

val MinecraftServer.cartridgeManager get() = dataStorage.computeIfAbsent(CartridgeManager.type)

val ServerLevel.cartridgeManager get() = server.cartridgeManager

var ServerPlayer.cartridgeStatus
    get() = level().cartridgeManager.get(uuid)
    set(value) { level().cartridgeManager.set(uuid, value) }