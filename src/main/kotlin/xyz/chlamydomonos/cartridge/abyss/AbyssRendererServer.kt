package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.network.PacketDistributor
import xyz.chlamydomonos.cartridge.utils.abyssManager

@EventBusSubscriber
object AbyssRendererServer {
    fun sendAbyssInitPacket(event: PlayerEvent) {
        val player = event.entity
        val level = player.level()
        if (level is ServerLevel && player is ServerPlayer) {
            PacketDistributor.sendToPlayer(player, AbyssInitPacket(level.abyssManager.root))
        }
    }

    @SubscribeEvent
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        sendAbyssInitPacket(event)
    }

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        sendAbyssInitPacket(event)
    }
}