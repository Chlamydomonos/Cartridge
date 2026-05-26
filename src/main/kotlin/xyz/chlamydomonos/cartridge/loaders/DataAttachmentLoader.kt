package xyz.chlamydomonos.cartridge.loaders

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.hollow.HollowEntityData
import xyz.chlamydomonos.cartridge.abyss.ChunkAbyssData
import java.util.*

object DataAttachmentLoader {
    val registry = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Cartridge.ID)

    val CHUNK_ABYSS = registry.register("chunk_abyss") { ->
        AttachmentType
            .builder { -> ChunkAbyssData() }
            .serialize(ChunkAbyssData.codec.fieldOf("abyss"))
            .build()
    }

    val HOLLOW_UUID = registry.register("hollow_uuid") { ->
        AttachmentType
            .builder { -> Optional.empty<UUID>() }
            .serialize(UUIDUtil.CODEC.optionalFieldOf("hollow_uuid"))
            .sync(ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC))
            .build()
    }

    val IS_DEAD_HOLLOW = registry.register("is_dead_hollow") { ->
        AttachmentType
            .builder { -> false }
            .serialize(Codec.BOOL.fieldOf("is_dead_hollow"))
            .sync(ByteBufCodecs.BOOL)
            .build()
    }

    val LAST_POS = registry.register("last_pos") { ->
        AttachmentType
            .builder { -> BlockPos(0, -100000, 0) }
            .build()
    }

    val MAX_DEPTH = registry.register("max_depth") { ->
        AttachmentType
            .builder { -> Optional.empty<Int>() }
            .serialize(Codec.INT.optionalFieldOf("max_depth"))
            .build()
    }

    val MAX_ABYSS_LEVEL = registry.register("max_abyss_level") { ->
        AttachmentType
            .builder { -> 0 }
            .serialize(Codec.INT.fieldOf("max_abyss_level"))
            .build()
    }

    val SURGERY_TABLE_POS = registry.register("surgery_table_pos") { ->
        AttachmentType
            .builder { -> Optional.empty<BlockPos>() }
            .sync(ByteBufCodecs.optional(BlockPos.STREAM_CODEC))
            .build()
    }

    val HOLLOW_ENTITY_DATA = registry.register("hollow_entity_data") { ->
        AttachmentType
            .builder(HollowEntityData::default)
            .serialize(HollowEntityData.codec.fieldOf("hollow_data"))
            .sync(HollowEntityData.streamCodec)
            .build()
    }

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }
}
