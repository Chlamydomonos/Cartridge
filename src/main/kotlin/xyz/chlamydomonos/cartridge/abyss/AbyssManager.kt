package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.core.BlockPos
import net.minecraft.core.SectionPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.network.PacketDistributor
import xyz.chlamydomonos.cartridge.loaders.DataAttachmentLoader

class AbyssManager(val level: Level) {
    @Suppress("DuplicatedCode")
    fun setValue(from: BlockPos, to: BlockPos, value: Byte) {
        val minX = minOf(from.x, to.x)
        val minY = minOf(from.y, to.y)
        val minZ = minOf(from.z, to.z)
        val maxX = maxOf(from.x, to.x)
        val maxY = maxOf(from.y, to.y)
        val maxZ = maxOf(from.z, to.z)

        for (sx in SectionPos.blockToSectionCoord(minX)..SectionPos.blockToSectionCoord(maxX)) {
            for (sz in SectionPos.blockToSectionCoord(minZ)..SectionPos.blockToSectionCoord(maxZ)) {
                val chunk = level.getChunk(sx, sz)
                val data = chunk.getData(DataAttachmentLoader.CHUNK_ABYSS)
                var changed = false
                for (sy in SectionPos.blockToSectionCoord(minY)..SectionPos.blockToSectionCoord(maxY)) {
                    data.getRegion(sx, sy, sz).setValue(from, to, value)
                    changed = true
                }
                if (changed) {
                    chunk.setData(DataAttachmentLoader.CHUNK_ABYSS, data)
                    chunk.markUnsaved()
                }
            }
        }
        if (!level.isClientSide && level is ServerLevel) {
            PacketDistributor.sendToPlayersInDimension(level, AbyssUpdatePacket(from, to, (-1).toByte(), value))
        }
    }

    @Suppress("DuplicatedCode")
    fun replaceValue(from: BlockPos, to: BlockPos, fromValue: Byte, toValue: Byte) {
        val minX = minOf(from.x, to.x)
        val minY = minOf(from.y, to.y)
        val minZ = minOf(from.z, to.z)
        val maxX = maxOf(from.x, to.x)
        val maxY = maxOf(from.y, to.y)
        val maxZ = maxOf(from.z, to.z)

        for (sx in SectionPos.blockToSectionCoord(minX)..SectionPos.blockToSectionCoord(maxX)) {
            for (sz in SectionPos.blockToSectionCoord(minZ)..SectionPos.blockToSectionCoord(maxZ)) {
                val chunk = level.getChunk(sx, sz)
                val data = chunk.getData(DataAttachmentLoader.CHUNK_ABYSS)
                var changed = false
                for (sy in SectionPos.blockToSectionCoord(minY)..SectionPos.blockToSectionCoord(maxY)) {
                    val node = data.regions[sy]
                    if (node != null) {
                        node.replaceValue(from, to, fromValue, toValue)
                        changed = true
                    }
                }
                if (changed) {
                    chunk.setData(DataAttachmentLoader.CHUNK_ABYSS, data)
                    chunk.markUnsaved()
                }
            }
        }
        if (!level.isClientSide && level is ServerLevel) {
            PacketDistributor.sendToPlayersInDimension(level, AbyssUpdatePacket(from, to, fromValue, toValue))
        }
    }

    fun getValue(pos: BlockPos): Byte {
        val sectionPos = SectionPos.of(pos)
        val chunk = level.getChunk(sectionPos.x(), sectionPos.z())
        val data = chunk.getData(DataAttachmentLoader.CHUNK_ABYSS)
        val sy = sectionPos.y()
        val node = data.regions[sy] ?: return 0
        if (!node.isCustomRoot) {
            node.isCustomRoot = true
            node.customMinX = sectionPos.minBlockX()
            node.customMinY = sectionPos.minBlockY()
            node.customMinZ = sectionPos.minBlockZ()
        }
        return node.getValue(pos)
    }

    fun getVoxelShape(center: BlockPos, range: Int, value: Byte): VoxelShape {
        var result = Shapes.empty()

        val minX = center.x - range
        val minY = center.y - range
        val minZ = center.z - range
        val maxX = center.x + range
        val maxY = center.y + range
        val maxZ = center.z + range

        for (sx in SectionPos.blockToSectionCoord(minX)..SectionPos.blockToSectionCoord(maxX)) {
            for (sz in SectionPos.blockToSectionCoord(minZ)..SectionPos.blockToSectionCoord(maxZ)) {
                val chunk = level.getChunk(sx, sz)
                val data = chunk.getData(DataAttachmentLoader.CHUNK_ABYSS)
                for (sy in SectionPos.blockToSectionCoord(minY)..SectionPos.blockToSectionCoord(maxY)) {
                    val node = data.regions[sy]
                    if (node != null) {
                        if (!node.isCustomRoot) {
                            val sectionPos = SectionPos.of(sx, sy, sz)
                            node.isCustomRoot = true
                            node.customMinX = sectionPos.minBlockX()
                            node.customMinY = sectionPos.minBlockY()
                            node.customMinZ = sectionPos.minBlockZ()
                        }
                        result = Shapes.or(result, node.getVoxelShape(center, range, value))
                    }
                }
            }
        }
        return result
    }
}