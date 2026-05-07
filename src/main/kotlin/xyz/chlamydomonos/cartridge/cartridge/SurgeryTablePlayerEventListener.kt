package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

@EventBusSubscriber
object SurgeryTablePlayerEventListener {
    @SubscribeEvent
    fun onCanContinueSleeping(event: CanContinueSleepingEvent) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        event.setContinueSleeping(player.surgeryTablePos != null)
    }

    private fun removePlayer(player: ServerPlayer) {
        val surgeryTablePos = player.surgeryTablePos ?: return
        val be = player.level().getBlockEntity(surgeryTablePos)
        if (be !is SurgeryTableBlockEntity) {
            throw RuntimeException("Trying to access SurgeryTableBlockEntity without surgery table")
        }
        be.playerOn = null
        be.handlingPacket = false
    }

    @SubscribeEvent
    fun onPlayerWakeUp(event: PlayerWakeUpEvent) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        removePlayer(player)
        player.surgeryTablePos = null
    }

    @SubscribeEvent
    fun onPlayerLoggedOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        removePlayer(player)
    }
}