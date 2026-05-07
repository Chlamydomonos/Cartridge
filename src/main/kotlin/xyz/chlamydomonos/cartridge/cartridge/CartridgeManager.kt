package xyz.chlamydomonos.cartridge.cartridge

import com.mojang.serialization.Codec
import net.minecraft.core.UUIDUtil
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import xyz.chlamydomonos.cartridge.utils.RLUtil
import java.util.*

class CartridgeManager(
    val data: MutableMap<UUID, CartridgeStatus> = mutableMapOf()
) : SavedData() {
    enum class CartridgeStatus {
        NONE,
        FREE,
        EQUIPPED,
        DEAD;

        companion object {
            val codec = Codec.BYTE.xmap(
                { CartridgeStatus.entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
        }
    }

    fun get(key: UUID): CartridgeStatus {
        if (data[key] == null) {
            data[key] = CartridgeStatus.NONE
            setDirty()
        }
        return data[key]!!
    }

    fun set(key: UUID, value: CartridgeStatus) {
        data[key] = value
        setDirty()
    }

    companion object {
        val codec = Codec.unboundedMap(UUIDUtil.STRING_CODEC, CartridgeStatus.codec).xmap(
            { CartridgeManager(it.toMutableMap()) },
            { it.data }
        )

        val type = SavedDataType(RLUtil.of("cartridge_manager"), ::CartridgeManager, codec)
    }
}