package xyz.chlamydomonos.catridge.loaders

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import xyz.chlamydomonos.catridge.Cartridge
import java.util.*

object PlayerDataLoader {
    private val registry = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Cartridge.ID)

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

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }
}
