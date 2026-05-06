package xyz.chlamydomonos.cartridge.loaders

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.cartridge.Cartridge

object DataComponentLoader {
    private val registry = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Cartridge.ID)

    private val optionalBlockPos = registry.registerComponentType("optional_block_pos") { it
        .persistent(BlockPos.CODEC.optionalFieldOf("pos").codec())
        .networkSynchronized(ByteBufCodecs.optional(BlockPos.STREAM_CODEC))
    }
    val OPTIONAL_BLOCK_POS by optionalBlockPos

    private val optionalUUID = registry.registerComponentType("optional_uuid") { it
        .persistent(UUIDUtil.CODEC.optionalFieldOf("uuid").codec())
        .networkSynchronized(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC))
    }
    val OPTIONAL_UUID by optionalUUID

    private val optionalName = registry.registerComponentType("optional_name") { it
        .persistent(Codec.STRING.optionalFieldOf("name").codec())
        .networkSynchronized(ByteBufCodecs.optional(ByteBufCodecs.stringUtf8(128)))
    }
    val OPTIONAL_NAME by optionalName

    private val cartridgeDurability = registry.registerComponentType("cartridge_durability") { it
        .persistent(Codec.INT)
        .networkSynchronized(ByteBufCodecs.INT)
    }
    val CARTRIDGE_DURABILITY by cartridgeDurability

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }
}