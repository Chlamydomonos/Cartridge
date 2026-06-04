package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class GangwayRenderPacket(
    val beam: GangwayHandler.Beam
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<GangwayRenderPacket>(RLUtil.of("gangway_render"))

        val codec = GangwayHandler.Beam.codec.map(::GangwayRenderPacket) { it.beam }

        fun handle(packet: GangwayRenderPacket, context: IPayloadContext) {
            context.enqueueWork {
                GangwayBeamRenderer.beams.add(
                    GangwayBeamRenderer.Beam(
                        packet.beam.start,
                        packet.beam.end,
                        0,
                        GangwayBeamRenderer.LIFETIME
                    )
                )
            }
        }
    }

    override fun type() = type
}
