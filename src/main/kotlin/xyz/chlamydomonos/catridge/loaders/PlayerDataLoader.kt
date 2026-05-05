package xyz.chlamydomonos.catridge.loaders

import com.mojang.serialization.Codec
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import xyz.chlamydomonos.catridge.Catridge
import java.util.*

object PlayerDataLoader {
    private val registry = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Catridge.ID)

    val IS_CATRIDGE = registry.register("is_catridge") { ->
        AttachmentType
            .builder { -> false }
            .serialize(Codec.BOOL.fieldOf("is_catridge"))
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

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }
}
