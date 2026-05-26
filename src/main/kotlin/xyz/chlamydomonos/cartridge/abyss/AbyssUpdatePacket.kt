package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.abyssManager

class AbyssUpdatePacket(
    val from: BlockPos,
    val to: BlockPos,
    val fromValue: Byte,
    val toValue: Byte
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<AbyssUpdatePacket>(RLUtil.of("abyss_update"))

        val codec = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            AbyssUpdatePacket::from,
            BlockPos.STREAM_CODEC,
            AbyssUpdatePacket::to,
            ByteBufCodecs.BYTE,
            AbyssUpdatePacket::fromValue,
            ByteBufCodecs.BYTE,
            AbyssUpdatePacket::toValue,
            ::AbyssUpdatePacket
        )

        fun handle(packet: AbyssUpdatePacket, context: IPayloadContext) {
            context.enqueueWork {
                val level = net.minecraft.client.Minecraft.getInstance().level ?: return@enqueueWork
                val manager = level.abyssManager
                if (packet.fromValue.toInt() == -1) {
                    manager.setValue(packet.from, packet.to, packet.toValue)
                } else {
                    manager.replaceValue(packet.from, packet.to, packet.fromValue, packet.toValue)
                }
            }
        }
    }

    override fun type() = type
}