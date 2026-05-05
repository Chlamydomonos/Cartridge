package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.curse.CurseEffect
import xyz.chlamydomonos.cartridge.utils.abyssManager
import xyz.chlamydomonos.cartridge.utils.lastPos
import xyz.chlamydomonos.cartridge.utils.maxAbyssLevel
import xyz.chlamydomonos.cartridge.utils.maxDepth

@EventBusSubscriber
object AbyssPlayerHandler {
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        val level = player.level()
        if (level.isClientSide || player.isCreative || player.isSpectator) {
            return
        }

        val pos = player.blockPosition()
        if (player.lastPos == pos) {
            return
        }

        onPlayerMove(player as ServerPlayer, level as ServerLevel, pos)
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
                CurseEffect.apply(player, currentY - minY, maxAbyssLevel)
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

            CurseEffect.apply(player, currentY - minY, maxAbyssLevel)
            player.maxDepth = currentY
            return
        }

        if (currentY >= minY + 10) {
            CurseEffect.apply(player, currentY - minY, maxAbyssLevel)
            player.maxDepth = currentY
        }
    }
}