package xyz.chlamydomonos.cartridge.hollow

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import tschipp.carryon.common.carry.CarryOnDataManager
import xyz.chlamydomonos.cartridge.cartridge.CartridgePlayerEventListener
import xyz.chlamydomonos.cartridge.loaders.EntityLoader
import xyz.chlamydomonos.cartridge.utils.carryingHollow
import xyz.chlamydomonos.cartridge.utils.carryingHollowUUID
import xyz.chlamydomonos.cartridge.utils.hollowCarriedBy
import xyz.chlamydomonos.cartridge.utils.hollowCarriedByUUID

@EventBusSubscriber
object CarriedHollowEventListener {
    val hasCarryOn = Unit.let {
        try {
            Class.forName("tschipp.carryon.CarryOnNeoForge")
            true
        } catch (_: Exception) {
            false
        }
    }

    @SubscribeEvent
    fun onPlayerLoggedOutWrapper(event: PlayerEvent.PlayerLoggedOutEvent) {
        if (hasCarryOn) {
            onPlayerLoggedOut(event)
        }
    }

    private fun stopCarrying(player: Player) {
        val carryOnData = CarryOnDataManager.getCarryData(player)
        carryOnData.clear()
        CarryOnDataManager.setCarryData(player, carryOnData)
        player.removeEffect(MobEffects.SLOWNESS)
    }

    private fun handleCarried(player: ServerPlayer) {
        val carriedBy = player.hollowCarriedBy ?: return
        stopCarrying(carriedBy)
        carriedBy.removeEffect(MobEffects.SLOWNESS)
        carriedBy.carryingHollowUUID = null
    }

    private fun handleCarrying(player: ServerPlayer) {
        val carrying = player.carryingHollow ?: return
        if (carrying !is ServerPlayer) {
            return
        }

        carrying.hollowCarriedByUUID = null
        carrying.stopRiding()
        HollowEntity.create(carrying)
        stopCarrying(player)
    }

    fun onPlayerLoggedOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        handleCarried(player)
        handleCarrying(player)
    }

    @SubscribeEvent
    fun onPlayerTickWrapper(event: PlayerTickEvent.Pre) {
        if (hasCarryOn) {
            onPlayerTick(event)
        }
    }

    fun onPlayerTick(event: PlayerTickEvent.Pre) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        val carriedBy = player.hollowCarriedBy
        if (carriedBy !is ServerPlayer) {
            return
        }

        if (carriedBy.carryingHollowUUID != player.uuid) {
            stopCarrying(carriedBy)
            player.hollowCarriedByUUID = null
            HollowEntity.create(player)
        }

        val vehicle = player.vehicle
        if (vehicle !is CarriedHollowEntity || player.camera != carriedBy) {
            CartridgePlayerEventListener.trackPlayer(player, carriedBy, EntityLoader.CARRIED_HOLLOW)
        }
    }
}