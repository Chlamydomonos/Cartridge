package xyz.chlamydomonos.catridge.loaders

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.catridge.Cartridge

object DataComponentLoader {
    private val registry = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Cartridge.ID)

    private val optionalBlockPos = registry.registerComponentType("optional_block_pos") { it
        .persistent(BlockPos.CODEC.optionalFieldOf("pos").codec())
        .networkSynchronized(ByteBufCodecs.optional(BlockPos.STREAM_CODEC))
    }

    val OPTIONAL_BLOCK_POS by optionalBlockPos

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }
}