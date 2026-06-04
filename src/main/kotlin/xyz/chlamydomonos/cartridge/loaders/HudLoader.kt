package xyz.chlamydomonos.cartridge.loaders

import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import xyz.chlamydomonos.cartridge.cartridge.BackpackHud
import xyz.chlamydomonos.cartridge.gangway.GangwayHud
import xyz.chlamydomonos.cartridge.sparagmos.SparagmosHud
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber(value = [Dist.CLIENT])
object HudLoader {
    @SubscribeEvent
    fun onRegisterGuiLayers(event: RegisterGuiLayersEvent) {
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, RLUtil.of("backpack_hud"), BackpackHud)
        event.registerAbove(VanillaGuiLayers.PLAYER_HEALTH, RLUtil.of("sparagmos_hud"), SparagmosHud)
        event.registerAbove(RLUtil.of("sparagmos_hud"), RLUtil.of("gangway_hud"), GangwayHud)
    }
}