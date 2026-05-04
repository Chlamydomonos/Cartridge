package xyz.chlamydomonos.catridge.curse

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.catridge.utils.RLUtil

class ConfusionPacket : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<ConfusionPacket>(RLUtil.of("confusion"))
        val INSTANCE = ConfusionPacket()
        val codec = StreamCodec.unit<RegistryFriendlyByteBuf, ConfusionPacket>(INSTANCE)

        fun handle(packet: ConfusionPacket, context: IPayloadContext) {
            context.enqueueWork {
                CurseClientHandler.ConfusionHandler.confusionTime = 100
            }
        }
    }

    private constructor()
    override fun type() = type
}