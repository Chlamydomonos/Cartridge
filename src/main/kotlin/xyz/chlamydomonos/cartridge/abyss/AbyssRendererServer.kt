package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.core.SectionPos
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.level.ChunkWatchEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber
object AbyssRendererServer {

    @SubscribeEvent
    fun onChunkWatch(event: ChunkWatchEvent.Sent) {
        val player = event.player
        val level = event.level
        val cx = event.pos.x
        val cz = event.pos.z

        val minSection = SectionPos.blockToSectionCoord(level.minY)
        val maxSection = SectionPos.blockToSectionCoord(level.maxY - 1)

        val chunk = level.getChunk(cx, cz)
        val data = chunk.getData(xyz.chlamydomonos.cartridge.loaders.DataAttachmentLoader.CHUNK_ABYSS)
        val regionsToSend = mutableMapOf<Int, OctreeNode>()
        for (sy in minSection..maxSection) {
            val node = data.regions[sy]
            if (node != null) {
                regionsToSend[sy] = node
            }
        }
        if (regionsToSend.isNotEmpty()) {
            PacketDistributor.sendToPlayer(player, AbyssInitPacket(cx, cz, regionsToSend))
        }
    }
}