package xyz.chlamydomonos.catridge.curse

import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.catridge.utils.RLUtil
import java.util.*

data class VomitPacket(
    val entityID: UUID
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<VomitPacket>(RLUtil.of("vomit"))

        val codec = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            VomitPacket::entityID,
            ::VomitPacket
        )

        fun handle(packet: VomitPacket, context: IPayloadContext) {
            context.enqueueWork {
                CurseClientHandler.VomitHandler.vomitingEntities[packet.entityID] = 8
            }
        }
    }

    override fun type() = type
}