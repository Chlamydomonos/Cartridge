package xyz.chlamydomonos.cartridge.cartridge

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.utils.RLUtil
import java.util.*
import kotlin.jvm.optionals.getOrNull

class CartridgeManager(
    val data: MutableMap<UUID, CartridgeData> = mutableMapOf()
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

    data class CartridgeData(
        var status: CartridgeStatus = CartridgeStatus.NONE,
        var equipper: UUID? = null
    ) {
        companion object {
            val codec = RecordCodecBuilder.create { builder -> builder
                .group(
                    CartridgeStatus.codec.fieldOf("status").forGetter(CartridgeData::status),
                    UUIDUtil.CODEC.optionalFieldOf("equipper").forGetter { Optional.ofNullable(it.equipper) }
                )
                .apply(builder) { status, equipper ->
                    CartridgeData(status, equipper.getOrNull())
                }
            }
        }
    }

    fun get(key: UUID): CartridgeData {
        if (data[key] == null) {
            Cartridge.logger.debug("Get: Created cartridge data for {}", key)
            data[key] = CartridgeData()
            setDirty()
        }
        return data[key]!!
    }

    fun set(key: UUID, value: CartridgeData) {
        if (data[key] == null) {
            Cartridge.logger.debug("Set: Created cartridge data for {}", key)
        }
        data[key] = value
        setDirty()
    }

    fun setStatus(key: UUID, value: CartridgeStatus) {
        get(key).status = value
        setDirty()
    }

    fun setEquipper(key: UUID, value: UUID?) {
        get(key).equipper = value
        setDirty()
    }

    companion object {
        val codec = Codec.unboundedMap(UUIDUtil.STRING_CODEC, CartridgeData.codec).xmap(
            { CartridgeManager(it.toMutableMap()) },
            { it.data }
        )

        val type = SavedDataType(RLUtil.of("cartridge_manager"), ::CartridgeManager, codec)
    }
}