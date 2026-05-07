package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class CartridgeConfirmPacket(
    val doConfirm: Boolean
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<CartridgeConfirmPacket>(RLUtil.of("cartridge_confirm"))

        val codec = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            CartridgeConfirmPacket::doConfirm,
            ::CartridgeConfirmPacket
        )

        fun handle(@Suppress("unused") packet: CartridgeConfirmPacket, context: IPayloadContext) {
            context.enqueueWork {
                // TODO
            }
        }
    }

    override fun type() = type
}