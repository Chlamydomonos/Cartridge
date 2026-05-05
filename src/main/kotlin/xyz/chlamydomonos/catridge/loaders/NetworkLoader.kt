package xyz.chlamydomonos.catridge.loaders

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import xyz.chlamydomonos.catridge.Catridge
import xyz.chlamydomonos.catridge.curse.BloodPacket
import xyz.chlamydomonos.catridge.curse.ConfusionPacket
import xyz.chlamydomonos.catridge.curse.ExplosionPacket
import xyz.chlamydomonos.catridge.curse.IllusionPacket
import xyz.chlamydomonos.catridge.curse.VomitPacket

@EventBusSubscriber
object NetworkLoader {
    @SubscribeEvent
    fun onRegisterPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Catridge.ID)
        registrar.playToClient(VomitPacket.type, VomitPacket.codec, VomitPacket::handle)
        registrar.playToClient(IllusionPacket.type, IllusionPacket.codec, IllusionPacket::handle)
        registrar.playToClient(BloodPacket.type, BloodPacket.codec, BloodPacket::handle)
        registrar.playToClient(ConfusionPacket.type, ConfusionPacket.codec, ConfusionPacket::handle)
        registrar.playToClient(ExplosionPacket.type, ExplosionPacket.codec, ExplosionPacket::handle)
    }
}