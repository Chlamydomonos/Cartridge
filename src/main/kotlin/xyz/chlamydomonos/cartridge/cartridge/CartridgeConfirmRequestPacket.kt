package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.ScreenOpenWrapper
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos
import java.util.*

class CartridgeConfirmRequestPacket(
    val playerUUID: UUID
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<CartridgeConfirmRequestPacket>(RLUtil.of("cartridge_confirm_request"))
        val codec = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            CartridgeConfirmRequestPacket::playerUUID,
            ::CartridgeConfirmRequestPacket
        )
        fun handle(packet: CartridgeConfirmRequestPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                if (player.surgeryTablePos == null) {
                    return@enqueueWork
                }

                val source = player.level().getPlayerByUUID(packet.playerUUID) ?: return@enqueueWork
                ScreenOpenWrapper.openCartridgeCreationConfirmScreen(source.plainTextName)
            }
        }
    }

    override fun type() = type
}