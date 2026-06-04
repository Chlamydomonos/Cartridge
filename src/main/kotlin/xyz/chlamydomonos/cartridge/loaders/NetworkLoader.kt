package xyz.chlamydomonos.cartridge.loaders

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.abyss.AbyssInitPacket
import xyz.chlamydomonos.cartridge.abyss.AbyssUpdatePacket
import xyz.chlamydomonos.cartridge.cartridge.*
import xyz.chlamydomonos.cartridge.curse.*
import xyz.chlamydomonos.cartridge.gangway.GangwayInputPacket
import xyz.chlamydomonos.cartridge.gangway.GangwayRenderPacket
import xyz.chlamydomonos.cartridge.sparagmos.SparagmosInputPacket
import xyz.chlamydomonos.cartridge.sparagmos.SparagmosRenderPacket

@EventBusSubscriber
object NetworkLoader {
    @SubscribeEvent
    fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar("${Cartridge.ID}v${Cartridge.version}")
        registrar.playToClient(VomitPacket.type, VomitPacket.codec, VomitPacket::handle)
        registrar.playToClient(IllusionPacket.type, IllusionPacket.codec, IllusionPacket::handle)
        registrar.playToClient(BloodPacket.type, BloodPacket.codec, BloodPacket::handle)
        registrar.playToClient(ConfusionPacket.type, ConfusionPacket.codec, ConfusionPacket::handle)
        registrar.playToClient(ExplosionPacket.type, ExplosionPacket.codec, ExplosionPacket::handle)
        registrar.playToClient(AbyssUpdatePacket.type, AbyssUpdatePacket.codec, AbyssUpdatePacket::handle)
        registrar.playToClient(AbyssInitPacket.type, AbyssInitPacket.codec, AbyssInitPacket::handle)
        registrar.playToServer(CartridgeCreationRequestPacket.type, CartridgeCreationRequestPacket.codec, CartridgeCreationRequestPacket::handle)
        registrar.playToClient(CartridgeConfirmRequestPacket.type, CartridgeConfirmRequestPacket.codec, CartridgeConfirmRequestPacket::handle)
        registrar.playToServer(CartridgeConfirmPacket.type, CartridgeConfirmPacket.codec, CartridgeConfirmPacket::handle)
        registrar.playToClient(BecomeCartridgePacket.type, BecomeCartridgePacket.codec, BecomeCartridgePacket::handle)
        registrar.playToServer(SuicidePacket.type, SuicidePacket.codec, SuicidePacket::handle)
        registrar.playToServer(IsCartridgeRequestPacket.type, IsCartridgeRequestPacket.codec, IsCartridgeRequestPacket::handle)
        registrar.playToClient(IsCartridgeResponsePacket.type, IsCartridgeResponsePacket.codec, IsCartridgeResponsePacket::handle)
        registrar.playToClient(CartridgeUsePacket.type, CartridgeUsePacket.codec, CartridgeUsePacket::handle)
        registrar.playToServer(EjectCartridgePacket.type, EjectCartridgePacket.codec, EjectCartridgePacket::handle)
        registrar.playToServer(SparagmosInputPacket.type, SparagmosInputPacket.codec, SparagmosInputPacket::handle)
        registrar.playToClient(SparagmosRenderPacket.type, SparagmosRenderPacket.codec, SparagmosRenderPacket::handle)
        registrar.playToServer(GangwayInputPacket.type, GangwayInputPacket.codec, GangwayInputPacket::handle)
        registrar.playToClient(GangwayRenderPacket.type, GangwayRenderPacket.codec, GangwayRenderPacket::handle)
    }
}