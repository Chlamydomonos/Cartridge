package xyz.chlamydomonos.catridge.loaders

import com.mojang.serialization.Codec
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import xyz.chlamydomonos.catridge.Catridge

object PlayerDataLoader {
    private val registry = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Catridge.ID)

    val IS_CATRIDGE = registry.register("is_catridge") { ->
        AttachmentType
            .builder { -> false }
            .serialize(Codec.BOOL.fieldOf("is_catridge"))
            .build()
    }

    val IS_HOLLOW = registry.register("is_hollow") { ->
        AttachmentType
            .builder { -> false }
            .serialize(Codec.BOOL.fieldOf("is_hollow"))
            .build()
    }

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }
}