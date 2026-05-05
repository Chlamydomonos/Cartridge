package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class AbyssInitPacket(
    val root: OctreeNode
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<AbyssInitPacket>(RLUtil.of("abyss_init"))

        val codec = StreamCodec.composite(
            OctreeNode.streamCodec,
            AbyssInitPacket::root,
            ::AbyssInitPacket
        )

        fun handle(packet: AbyssInitPacket, context: IPayloadContext) {
            context.enqueueWork {
                AbyssRenderer.root = packet.root
            }
        }
    }

    override fun type() = type
}