package xyz.chlamydomonos.cartridge.curse

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

object ConfusionPacket : CustomPacketPayload {
    val type = CustomPacketPayload.Type<ConfusionPacket>(RLUtil.of("confusion"))
    val codec = StreamCodec.unit<RegistryFriendlyByteBuf, ConfusionPacket>(this)

    fun handle(@Suppress("unused") packet: ConfusionPacket, context: IPayloadContext) {
        context.enqueueWork {
            CurseClientHandler.ConfusionHandler.confusionTime = 100
        }
    }

    override fun type() = type
}