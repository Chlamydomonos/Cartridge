package xyz.chlamydomonos.catridge.curse

import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.catridge.utils.RLUtil
import java.util.*

class BloodPacket(
    val entityID: UUID
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<BloodPacket>(RLUtil.of("blood"))

        val codec = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            BloodPacket::entityID,
            ::BloodPacket
        )

        fun handle(packet: BloodPacket, context: IPayloadContext) {
            context.enqueueWork {
                CurseClientHandler.BloodHandler.bleedingEntities[packet.entityID] = 10
            }
        }
    }

    override fun type() = type
}