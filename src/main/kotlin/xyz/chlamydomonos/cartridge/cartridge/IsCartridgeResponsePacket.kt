package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class IsCartridgeResponsePacket(
    val value: Boolean
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<IsCartridgeResponsePacket>(RLUtil.of("is_cartridge_response"))

        val codec = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            IsCartridgeResponsePacket::value,
            ::IsCartridgeResponsePacket
        )

        fun handle(packet: IsCartridgeResponsePacket, context: IPayloadContext) {
            context.enqueueWork {
                CartridgePlayerClientEventListener.isCartridge = packet.value
                CartridgePlayerClientEventListener.requestPacketSent = false
            }
        }
    }

    override fun type() = type
}