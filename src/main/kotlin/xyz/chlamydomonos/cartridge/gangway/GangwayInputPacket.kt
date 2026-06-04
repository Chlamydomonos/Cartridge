package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.joml.Vector3fc
import xyz.chlamydomonos.cartridge.utils.RLUtil

class GangwayInputPacket(
    val pos: Vector3fc,
    val viewVector: Vector3fc,
    val isHeavyAttack: Boolean
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<GangwayInputPacket>(RLUtil.of("gangway_input"))

        val codec = StreamCodec.composite(
            ByteBufCodecs.VECTOR3F,
            GangwayInputPacket::pos,
            ByteBufCodecs.VECTOR3F,
            GangwayInputPacket::viewVector,
            ByteBufCodecs.BOOL,
            GangwayInputPacket::isHeavyAttack,
            ::GangwayInputPacket
        )

        fun handle(packet: GangwayInputPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                if (player !is ServerPlayer) {
                    return@enqueueWork
                }

                if (packet.isHeavyAttack) {
                    GangwayHandler.heavyAttack(player, packet.pos, packet.viewVector)
                } else {
                    GangwayHandler.lightAttack(player, packet.pos, packet.viewVector)
                }
            }
        }
    }

    override fun type() = type
}