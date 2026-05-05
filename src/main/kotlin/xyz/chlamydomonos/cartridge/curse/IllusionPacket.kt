package xyz.chlamydomonos.cartridge.curse

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class IllusionPacket : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<IllusionPacket>(RLUtil.of("illusion"))
        val INSTANCE = IllusionPacket()
        val codec = StreamCodec.unit<RegistryFriendlyByteBuf, IllusionPacket>(INSTANCE)

        fun handle(@Suppress("unused") packet: IllusionPacket, context: IPayloadContext) {
            context.enqueueWork {
                IllusionHandler.addGhosts()
            }
        }
    }

    private constructor()
    override fun type() = type
}