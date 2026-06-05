package xyz.chlamydomonos.cartridge.abyss

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.nio.ByteBuffer

class OctreeNode() {
    // 该节点的层级。0为最高层，MAX_LAYER为最低层。每个最低层节点代表一个方块
    // 每个节点代表的具体区域由layer以及x，y，z隐式确定。layer为0的节点代表一个中心位于坐标原点的立方体，其他节点的坐标依此类推
    var layer: Byte = 0
    var x = false
    var y = false
    var z = false
    // 对于叶子节点，value为值。对于非叶子节点，value为一个bitmap，保存其子节点的信息。
    var value: Byte = 0
    var parent: OctreeNode? = null

    var isCustomRoot = false
    var customMinX = 0
    var customMinY = 0
    var customMinZ = 0

    val children = Array<OctreeNode?>(8) { null }

    fun serialize(data: MutableList<Byte>): MutableList<Byte> {
        data.add(value)
        if (value < 0) {
            for (child in children) {
                child!!.serialize(data)
            }
        }
        return data
    }

    fun serialize(): ByteBuffer {
        val data = mutableListOf(layer)
        serialize(data)
        return ByteBuffer.wrap(data.toByteArray())
    }

    fun toArray(): ByteArray {
        val data = mutableListOf(layer)
        serialize(data)
        return data.toByteArray()
    }

    private fun deserialize(data: ByteBuffer, fromIndex: Int, currentLayer: Byte): Int {
        var currentIndex = fromIndex
        layer = currentLayer
        value = data[currentIndex]
        currentIndex++
        if (value < 0) {
            for (i in 0..<8) {
                val child = OctreeNode()
                child.x = (i and 1) != 0
                child.y = (i and 2) != 0
                child.z = (i and 4) != 0
                currentIndex = child.deserialize(data, currentIndex, (currentLayer + 1).toByte())
                children[i] = child
                child.parent = this
            }
        }
        return currentIndex
    }

    fun deserialize(data: ByteBuffer) {
        deserialize(data, 1, 0)
    }

    fun fromArray(data: ByteArray): OctreeNode {
        deserialize(ByteBuffer.wrap(data))
        return this
    }

    constructor(data: ByteBuffer) : this() {
        deserialize(data)
    }

    private fun getCenterOffset(i: Int, size: Int): IntArray {
        val half = size / 2
        val dx = if ((i and 1) != 0) half else 0
        val dy = if ((i and 2) != 0) half else 0
        val dz = if ((i and 4) != 0) half else 0
        return intArrayOf(dx, dy, dz)
    }

    private fun getChildBounds(parentBounds: IntArray, childIndex: Int, parentSize: Int): IntArray {
        val half = parentSize / 2
        val minX = parentBounds[0] + if ((childIndex and 1) != 0) half else 0
        val minY = parentBounds[1] + if ((childIndex and 2) != 0) half else 0
        val minZ = parentBounds[2] + if ((childIndex and 4) != 0) half else 0
        return intArrayOf(minX, minY, minZ, minX + half - 1, minY + half - 1, minZ + half - 1)
    }

    private fun intersects(min1: IntArray, min2: IntArray): Boolean {
        return min1[0] <= min2[3] && min1[3] >= min2[0] &&
               min1[1] <= min2[4] && min1[4] >= min2[1] &&
               min1[2] <= min2[5] && min1[5] >= min2[2]
    }

    private fun contains(outer: IntArray, inner: IntArray): Boolean {
        return outer[0] <= inner[0] && outer[3] >= inner[3] &&
               outer[1] <= inner[1] && outer[4] >= inner[4] &&
               outer[2] <= inner[2] && outer[5] >= inner[5]
    }

    private fun split() {
        if (children[0] != null || layer >= MAX_LAYER) return
        for (i in 0..7) {
            val child = OctreeNode()
            child.layer = (layer + 1).toByte()
            child.x = (i and 1) != 0
            child.y = (i and 2) != 0
            child.z = (i and 4) != 0
            child.value = this.value
            child.parent = this
            children[i] = child
        }
        updateBitmap()
    }

    private fun updateBitmap() {
        if (children[0] != null) {
            var mask = 0
            for (i in 0..7) {
                val cv = children[i]!!.value.toInt()
                mask = if (cv < 0) {
                    mask or (cv and 0x7F)
                } else {
                    mask or (1 shl cv)
                }
            }
            value = (mask or 0x80).toByte()
        }
    }

    private fun merge() {
        if (children[0] == null) return
        var canMerge = true
        val firstVal = children[0]!!.value
        for (i in 0..7) {
            val child = children[i]!!
            child.merge()
            if (child.children[0] != null || child.value != firstVal) {
                canMerge = false
            }
        }
        if (canMerge) {
            this.value = firstVal
            for (i in 0..7) {
                children[i] = null
            }
        } else {
            updateBitmap()
        }
    }

    private fun setValueRecursive(targetBounds: IntArray, nodeBounds: IntArray, newValue: Byte) {
        if (contains(targetBounds, nodeBounds)) {
            value = newValue
            for (i in 0..7) children[i] = null
            return
        }
        split()
        if (children[0] == null) {
            value = newValue
            return
        }
        val size = 1 shl (MAX_LAYER - layer)
        for (i in 0..7) {
            val childBounds = getChildBounds(nodeBounds, i, size)
            if (intersects(targetBounds, childBounds)) {
                children[i]!!.setValueRecursive(targetBounds, childBounds, newValue)
            }
        }
        updateBitmap()
    }

    private fun getRootBounds(): IntArray {
        var root = this
        while (root.parent != null) root = root.parent!!
        if (root.isCustomRoot) {
            val size = 1 shl (MAX_LAYER - root.layer)
            return intArrayOf(
                root.customMinX, root.customMinY, root.customMinZ,
                root.customMinX + size - 1, root.customMinY + size - 1, root.customMinZ + size - 1
            )
        }
        if (root.layer >= MAX_LAYER) return intArrayOf(0, 0, 0, 0, 0, 0)
        val halfSize = 1 shl (MAX_LAYER - root.layer - 1)
        return intArrayOf(-halfSize, -halfSize, -halfSize, halfSize - 1, halfSize - 1, halfSize - 1)
    }

    private fun getMyBounds(): IntArray {
        if (parent == null) return getRootBounds()
        val pBounds = parent!!.getMyBounds()
        val size = 1 shl (MAX_LAYER - layer)
        val offset = getCenterOffset((if (x) 1 else 0) or (if (y) 2 else 0) or (if (z) 4 else 0), size * 2)
        return intArrayOf(
            pBounds[0] + offset[0], pBounds[1] + offset[1], pBounds[2] + offset[2],
            pBounds[0] + offset[0] + size - 1, pBounds[1] + offset[1] + size - 1, pBounds[2] + offset[2] + size - 1
        )
    }

    fun setValue(from: BlockPos, to: BlockPos, value: Byte) {
        val targetBounds = intArrayOf(
            minOf(from.x, to.x), minOf(from.y, to.y), minOf(from.z, to.z),
            maxOf(from.x, to.x), maxOf(from.y, to.y), maxOf(from.z, to.z)
        )
        val myBounds = getMyBounds()
        if (intersects(targetBounds, myBounds)) {
            setValueRecursive(targetBounds, myBounds, value)
            merge()
        }
    }

    private fun replaceValueRecursive(targetBounds: IntArray, nodeBounds: IntArray, fromValue: Byte, toValue: Byte) {
        if (children[0] == null) {
            if (value == fromValue) {
                setValueRecursive(targetBounds, nodeBounds, toValue)
            }
            return
        }
        val size = 1 shl (MAX_LAYER - layer)
        for (i in 0..7) {
            val childBounds = getChildBounds(nodeBounds, i, size)
            if (intersects(targetBounds, childBounds)) {
                children[i]!!.replaceValueRecursive(targetBounds, childBounds, fromValue, toValue)
            }
        }
        updateBitmap()
    }

    fun replaceValue(from: BlockPos, to: BlockPos, fromValue: Byte, toValue: Byte) {
        val targetBounds = intArrayOf(
            minOf(from.x, to.x), minOf(from.y, to.y), minOf(from.z, to.z),
            maxOf(from.x, to.x), maxOf(from.y, to.y), maxOf(from.z, to.z)
        )
        val myBounds = getMyBounds()
        if (intersects(targetBounds, myBounds)) {
            replaceValueRecursive(targetBounds, myBounds, fromValue, toValue)
            merge()
        }
    }

    fun getValue(pos: BlockPos): Byte {
        val bounds = getMyBounds()
        val pBounds = intArrayOf(pos.x, pos.y, pos.z, pos.x, pos.y, pos.z)
        if (!contains(bounds, pBounds)) return -1

        var current = this
        var cb = bounds
        while (current.children[0] != null) {
            val size = 1 shl (MAX_LAYER - current.layer)
            var idx = 0
            val midX = cb[0] + size / 2
            val midY = cb[1] + size / 2
            val midZ = cb[2] + size / 2
            if (pos.x >= midX) idx = idx or 1
            if (pos.y >= midY) idx = idx or 2
            if (pos.z >= midZ) idx = idx or 4

            cb = getChildBounds(cb, idx, size)

            current = current.children[idx]!!
        }
        return current.value
    }

    private fun collectShapes(bounds: IntArray, targetBounds: IntArray, targetValue: Byte, shapes: MutableList<VoxelShape>) {
        if ((value < 0 && (value.toInt() and (1 shl targetValue.toInt())) == 0)) return
        if (!intersects(bounds, targetBounds)) return
        if (children[0] == null) {
            if (value == targetValue) {
                val minX = maxOf(bounds[0], targetBounds[0]).toDouble()
                val minY = maxOf(bounds[1], targetBounds[1]).toDouble()
                val minZ = maxOf(bounds[2], targetBounds[2]).toDouble()
                val maxX = minOf(bounds[3], targetBounds[3]).toDouble() + 1.0
                val maxY = minOf(bounds[4], targetBounds[4]).toDouble() + 1.0
                val maxZ = minOf(bounds[5], targetBounds[5]).toDouble() + 1.0
                shapes.add(Shapes.box(minX, minY, minZ, maxX, maxY, maxZ))
            }
            return
        }
        val size = 1 shl (MAX_LAYER - layer)
        for (i in 0..7) {
            val childBounds = getChildBounds(bounds, i, size)
            children[i]!!.collectShapes(childBounds, targetBounds, targetValue, shapes)
        }
    }

    fun getVoxelShape(center: BlockPos, range: Int, value: Byte): VoxelShape {
        val targetBounds = intArrayOf(
            center.x - range, center.y - range, center.z - range,
            center.x + range, center.y + range, center.z + range
        )
        val shapes = mutableListOf<VoxelShape>()
        collectShapes(getMyBounds(), targetBounds, value, shapes)

        var result = Shapes.empty()
        for (shape in shapes) {
            result = Shapes.or(result, shape)
        }
        return result
    }

    companion object {
        const val MAX_LAYER = 4

        val codec = RecordCodecBuilder.create {
            it.group(
                Codec.BYTE_BUFFER.fieldOf("data").forGetter(OctreeNode::serialize)
            ).apply(it, ::OctreeNode)
        }!!

        val streamCodec = StreamCodec.composite(
            ByteBufCodecs.BYTE_ARRAY,
            { it.toArray() },
            { OctreeNode().fromArray(it) }
        )
    }
}