package xyz.chlamydomonos.cartridge.sparagmos

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.joml.Vector3f
import org.joml.Vector3fc
import xyz.chlamydomonos.cartridge.utils.RLUtil

class SparagmosInputPacket(
    val pos: Vector3fc,
    val pitch: Float,
    val yaw: Float,
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<SparagmosInputPacket>(RLUtil.of("sparagmos_input"))

        val codec = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            SparagmosInputPacket::pos,
            ByteBufCodecs.FLOAT,
            SparagmosInputPacket::pitch,
            ByteBufCodecs.FLOAT,
            SparagmosInputPacket::yaw,
            ::SparagmosInputPacket
        )

        fun handle(packet: SparagmosInputPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                if (player !is ServerPlayer) {
                    return@enqueueWork
                }
                SparagmosHandler.handle(player, Vector3f(packet.pos), packet.pitch, packet.yaw)
            }
        }
    }

    override fun type() = type
}