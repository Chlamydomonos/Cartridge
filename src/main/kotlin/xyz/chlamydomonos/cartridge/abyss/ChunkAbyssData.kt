package xyz.chlamydomonos.cartridge.abyss

import com.mojang.serialization.Codec
import net.minecraft.core.SectionPos

class ChunkAbyssData(
    val regions: MutableMap<Int, OctreeNode> = mutableMapOf()
) {
    fun getRegion(cx: Int, sy: Int, cz: Int): OctreeNode {
        val node = regions.getOrPut(sy) {
            val node = OctreeNode()
            node.layer = (OctreeNode.MAX_LAYER - 4).toByte()
            node
        }
        if (!node.isCustomRoot) {
            val sectionPos = SectionPos.of(cx, sy, cz)
            node.isCustomRoot = true
            node.customMinX = sectionPos.minBlockX()
            node.customMinY = sectionPos.minBlockY()
            node.customMinZ = sectionPos.minBlockZ()
        }
        return node
    }

    companion object {
        val codec = Codec.unboundedMap(Codec.STRING, OctreeNode.codec).xmap(
            { map -> ChunkAbyssData(map.mapKeys { it.key.toInt() }.toMutableMap()) },
            { data -> data.regions.mapKeys { it.key.toString() } }
        )
    }
}