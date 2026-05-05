package xyz.chlamydomonos.cartridge.abyss

import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import net.neoforged.neoforge.network.PacketDistributor
import xyz.chlamydomonos.cartridge.utils.RLUtil

class AbyssManager(
    val root: OctreeNode = OctreeNode()
) : SavedData() {
    fun setValue(level: ServerLevel, from: BlockPos, to: BlockPos, value: Byte) {
        root.setValue(from, to, value)
        PacketDistributor.sendToPlayersInDimension(level, AbyssUpdatePacket(from, to, (-1).toByte(), value))
        setDirty()
    }

    fun replaceValue(level: ServerLevel, from: BlockPos, to: BlockPos, fromValue: Byte, toValue: Byte) {
        root.replaceValue(from, to, fromValue, toValue)
        PacketDistributor.sendToPlayersInDimension(level, AbyssUpdatePacket(from, to, fromValue, toValue))
        setDirty()
    }

    fun getValue(pos: BlockPos): Byte {
        return root.getValue(pos)
    }

    companion object {
        val codec = RecordCodecBuilder.create {
            it.group(
                OctreeNode.codec.fieldOf("root").forGetter(AbyssManager::root)
            ).apply(it, ::AbyssManager)
        }!!

        val type = SavedDataType<AbyssManager>(RLUtil.of("abyss_manager"), ::AbyssManager, codec)
    }
}