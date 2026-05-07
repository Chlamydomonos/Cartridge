package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.ScreenOpenWrapper

object BecomeCartridgePacket : CustomPacketPayload {
    val type = CustomPacketPayload.Type<BecomeCartridgePacket>(RLUtil.of("become_cartridge"))
    val codec = StreamCodec.unit<RegistryFriendlyByteBuf, BecomeCartridgePacket>(this)
    fun handle(@Suppress("unused") packet: BecomeCartridgePacket, context: IPayloadContext) {
        context.enqueueWork {
            ScreenOpenWrapper.openCartridgeScreen()
        }
    }

    override fun type() = type
}