package xyz.chlamydomonos.cartridge.curse

import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import java.util.*

data class ExplosionPacket(
    val entityID: UUID
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<ExplosionPacket>(RLUtil.of("explosion"))

        val codec = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            ExplosionPacket::entityID,
            ::ExplosionPacket
        )

        fun handle(packet: ExplosionPacket, context: IPayloadContext) {
            context.enqueueWork {
                CurseClientHandler.ExplosionHandler.explodingEntities[packet.entityID] = 4
            }
        }
    }

    override fun type() = type
}