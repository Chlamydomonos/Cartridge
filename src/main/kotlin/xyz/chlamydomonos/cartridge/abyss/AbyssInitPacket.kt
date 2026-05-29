package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

class AbyssInitPacket(
    val chunkX: Int,
    val chunkZ: Int,
    val regions: Map<Int, OctreeNode>
) : CustomPacketPayload {
    companion object {
        val type = CustomPacketPayload.Type<AbyssInitPacket>(RLUtil.of("abyss_init"))

        val codec = StreamCodec.composite(
            ByteBufCodecs.INT,
            AbyssInitPacket::chunkX,
            ByteBufCodecs.INT,
            AbyssInitPacket::chunkZ,
            ByteBufCodecs.map({ HashMap(it) }, ByteBufCodecs.INT, OctreeNode.streamCodec),
            AbyssInitPacket::regions,
            ::AbyssInitPacket
        )

        fun handle(packet: AbyssInitPacket, context: IPayloadContext) {
            context.enqueueWork {
                val level = net.minecraft.client.Minecraft.getInstance().level ?: return@enqueueWork
                val chunk = level.getChunk(packet.chunkX, packet.chunkZ)
                val data = chunk.getData(xyz.chlamydomonos.cartridge.loaders.DataAttachmentLoader.CHUNK_ABYSS)
                packet.regions.forEach { (sy, node) ->
                    val sectionPos = net.minecraft.core.SectionPos.of(packet.chunkX, sy, packet.chunkZ)
                    node.isCustomRoot = true
                    node.customMinX = sectionPos.minBlockX()
                    node.customMinY = sectionPos.minBlockY()
                    node.customMinZ = sectionPos.minBlockZ()
                    data.regions[sy] = node
                }
            }
        }
    }

    override fun type() = type
}