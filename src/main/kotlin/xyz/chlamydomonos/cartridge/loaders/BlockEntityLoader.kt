package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlockEntity

object BlockEntityLoader {
    private val registry = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Cartridge.ID)

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val SURGERY_TABLE by registry.register("surgery_table") { ->
        BlockEntityType(::SurgeryTableBlockEntity, BlockLoader.SURGERY_TABLE)
    }
}