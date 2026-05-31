package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import xyz.chlamydomonos.cartridge.combat.CombatModeKeyMappings
import xyz.chlamydomonos.cartridge.utils.inCombatMode

@EventBusSubscriber(value = [Dist.CLIENT])
object BackpackClientEventListener {
    @SubscribeEvent
    fun onClientTick(@Suppress("unused") event: ClientTickEvent.Post) {
        val player = Minecraft.getInstance().player ?: return
        if (!player.inCombatMode) {
            return
        }

        while (CombatModeKeyMappings.EJECT_CARTRIDGE.consumeClick()) {
            ClientPacketDistributor.sendToServer(EjectCartridgePacket)
        }
    }
}