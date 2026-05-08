package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import top.theillusivec4.curios.api.event.CurioChangeEvent
import xyz.chlamydomonos.cartridge.loaders.ItemLoader

@EventBusSubscriber
object BackpackEventListener {
    @SubscribeEvent
    fun onCurioChange(event: CurioChangeEvent.Item) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        val from = event.from
        val to = event.to
        if (to.`is`(ItemLoader.BACKPACK)) {
            CartridgeHandler.iterateCartridges(to) {
                CartridgeHandler.onEquip(player, it)
            }
        } else if (from.`is`(ItemLoader.BACKPACK)) {
            CartridgeHandler.iterateCartridges(from) {
                CartridgeHandler.onUnequip(player, it)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerLoggedOut(event: PlayerEvent.PlayerLoggedOutEvent) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        val backpack = CartridgeHandler.getBackpack(player) ?: return
        CartridgeHandler.iterateCartridges(backpack) {
            CartridgeHandler.onUnequip(player, it)
        }
    }
}