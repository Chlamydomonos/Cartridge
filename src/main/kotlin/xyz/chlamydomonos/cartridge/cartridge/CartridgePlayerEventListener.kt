package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.utils.cartridgeDimension
import xyz.chlamydomonos.cartridge.utils.cartridgeStatus
import xyz.chlamydomonos.cartridge.utils.isCartridgeDimension

@EventBusSubscriber
object CartridgePlayerEventListener {
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        val level = player.level()
        if (player.cartridgeStatus == CartridgeManager.CartridgeStatus.FREE && !level.isCartridgeDimension) {
            player.teleportTo(
                level.cartridgeDimension,
                level.random.nextDouble() * 100,
                100.0,
                level.random.nextDouble() * 100,
                setOf(),
                player.yRot,
                player.xRot,
                false
            )
        }
    }
}