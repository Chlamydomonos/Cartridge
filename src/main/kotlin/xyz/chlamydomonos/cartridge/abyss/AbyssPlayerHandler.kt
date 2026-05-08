package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.cartridge.CartridgeHandler
import xyz.chlamydomonos.cartridge.curse.CurseEffect
import xyz.chlamydomonos.cartridge.utils.*

@EventBusSubscriber
object AbyssPlayerHandler {
    fun applyCurse(player: ServerPlayer, time: Int, curseLevel: Int) {
        if (!CartridgeHandler.tryUseCartridge(player, time, curseLevel)) {
            CurseEffect.apply(player, time, curseLevel)
        }
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        val level = player.level()
        if (level.isClientSide || player.isCreative || player.isSpectator || player !is ServerPlayer) {
            return
        }

        val pos = player.blockPosition()
        if (player.lastPos == pos) {
            return
        }

        if (player.isCartridge) {
            return
        }

        onPlayerMove(player, level as ServerLevel, pos)
        player.lastPos = pos
    }

    fun onPlayerMove(player: ServerPlayer, level: ServerLevel, pos: BlockPos) {
        val abyssManager = level.abyssManager
        val currentAbyssLevel = abyssManager.getValue(pos).toInt()
        val currentY = pos.y
        val minY = player.maxDepth
        val maxAbyssLevel = player.maxAbyssLevel

        if (currentAbyssLevel == 0) {
            if (maxAbyssLevel == 0 || minY == null) {
                return
            }

            if (currentY > minY) {
                applyCurse(player, currentY - minY, maxAbyssLevel)
            }

            player.maxAbyssLevel = 0
            player.maxDepth = null
            return
        }

        if (minY == null || currentY < minY) {
            player.maxDepth = currentY
            if (currentAbyssLevel > maxAbyssLevel) {
                player.maxAbyssLevel = currentAbyssLevel
            }
            return
        }

        if (currentAbyssLevel > maxAbyssLevel) {
            player.maxAbyssLevel = currentAbyssLevel
            return
        }

        if (currentAbyssLevel < maxAbyssLevel) {
            player.maxAbyssLevel = currentAbyssLevel
            if (currentY == minY) {
                return
            }

            applyCurse(player, currentY - minY, maxAbyssLevel)
            player.maxDepth = currentY
            return
        }

        if (currentY >= minY + 10) {
            applyCurse(player, currentY - minY, maxAbyssLevel)
            player.maxDepth = currentY
        }
    }
}