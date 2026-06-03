package xyz.chlamydomonos.cartridge.sparagmos

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.joml.Vector3f
import org.joml.Vector3fc
import xyz.chlamydomonos.cartridge.utils.RLUtil

class SparagmosRenderPacket(
    val pos: Vector3fc,
    val pitch: Float,
    val yaw: Float
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<SparagmosRenderPacket>(RLUtil.of("sparagmos_render"))

        val codec = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            SparagmosRenderPacket::pos,
            ByteBufCodecs.FLOAT,
            SparagmosRenderPacket::pitch,
            ByteBufCodecs.FLOAT,
            SparagmosRenderPacket::yaw,
            ::SparagmosRenderPacket
        )

        fun handle(packet: SparagmosRenderPacket, context: IPayloadContext) {
            context.enqueueWork {
                SparagmosLaserRenderer.beams.add(
                    SparagmosLaserRenderer.Beam(
                        Vector3f(packet.pos),
                        packet.pitch,
                        packet.yaw,
                        0,
                        SparagmosHandler.LIFETIME
                    )
                )
            }
        }
    }

    override fun type() = type
}