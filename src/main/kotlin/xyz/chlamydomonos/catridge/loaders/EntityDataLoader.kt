package xyz.chlamydomonos.catridge.loaders

import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.syncher.EntityDataSerializer
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.catridge.Catridge
import java.util.*
import kotlin.jvm.optionals.getOrNull

object EntityDataLoader {
    private val registry = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Catridge.ID)

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val UUID by registry.register("uuid") { ->
        object : EntityDataSerializer<Optional<UUID>> {
            override fun codec() = ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC)
            override fun copy(value: Optional<UUID>) = Optional.ofNullable(value.getOrNull())
        }
    }
}