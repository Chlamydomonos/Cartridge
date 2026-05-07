package xyz.chlamydomonos.cartridge.loaders

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.abyss.AbyssInitPacket
import xyz.chlamydomonos.cartridge.abyss.AbyssUpdatePacket
import xyz.chlamydomonos.cartridge.cartridge.BecomeCartridgePacket
import xyz.chlamydomonos.cartridge.cartridge.CartridgeConfirmPacket
import xyz.chlamydomonos.cartridge.cartridge.CartridgeConfirmRequestPacket
import xyz.chlamydomonos.cartridge.cartridge.CartridgeCreationRequestPacket
import xyz.chlamydomonos.cartridge.cartridge.SuicidePacket
import xyz.chlamydomonos.cartridge.curse.*

@EventBusSubscriber
object NetworkLoader {
    @SubscribeEvent
    fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Cartridge.ID)
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
    }
}