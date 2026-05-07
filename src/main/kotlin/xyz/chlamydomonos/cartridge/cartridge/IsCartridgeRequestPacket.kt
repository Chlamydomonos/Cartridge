package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.isCartridge

object IsCartridgeRequestPacket : CustomPacketPayload {
    val type = CustomPacketPayload.Type<IsCartridgeRequestPacket>(RLUtil.of("is_cartridge_request"))
    val codec = StreamCodec.unit<RegistryFriendlyByteBuf, IsCartridgeRequestPacket>(this)

    fun handle(@Suppress("unused") packet: IsCartridgeRequestPacket, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player()
            if (player !is ServerPlayer) {
                return@enqueueWork
            }

            PacketDistributor.sendToPlayer(
                player,
                IsCartridgeResponsePacket(player.isCartridge)
            )
        }
    }

    override fun type() = type
}