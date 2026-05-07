package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class CartridgeCreationRequestPacket : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<CartridgeCreationRequestPacket>(
            RLUtil.of("cartridge_creation_request")
        )
        val INSTANCE = CartridgeCreationRequestPacket()
        val codec = StreamCodec.unit<RegistryFriendlyByteBuf, CartridgeCreationRequestPacket>(INSTANCE)
        fun handle(@Suppress("unused") packet: CartridgeCreationRequestPacket, context: IPayloadContext) {
            context.enqueueWork {
                val player = context.player()
                val menu = player.containerMenu
                if (menu !is SurgeryTableMenu) {
                    return@enqueueWork
                }

                if (menu.inputContainer.getResource(0).isEmpty) {
                    return@enqueueWork
                }

                val be = menu.inputContainer.blockEntity ?: return@enqueueWork
                val playerOn = be.playerOn ?: return@enqueueWork
                be.handlingPacket = true
                PacketDistributor.sendToPlayer(playerOn, CartridgeConfirmRequestPacket(player.uuid))
            }
        }
    }

    private constructor()

    override fun type() = type
}