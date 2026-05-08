package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

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

        fun handle(packet: CartridgeConfirmPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                if (player !is ServerPlayer) {
                    return@enqueueWork
                }

                val be = player.level().getBlockEntity(player.surgeryTablePos ?: return@enqueueWork)
                if (be !is SurgeryTableBlockEntity) {
                    return@enqueueWork
                }

                val user = be.playerUsing ?: return@enqueueWork
                val menu = user.containerMenu
                if (menu !is SurgeryTableMenu) {
                    return@enqueueWork
                }

                if (packet.doConfirm) {
                    be.createCartridge()
                } else {
                    menu.refused = true
                }
            }
        }
    }

    override fun type() = type
}