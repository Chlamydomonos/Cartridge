package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.ai.village.poi.PoiType
import net.minecraft.world.level.block.state.properties.BedPart
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock

object PoiLoader {
    val registry = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, Cartridge.ID)

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val SURGERY_TABLE = registry.register("surgery_table") { ->
        PoiType(
            BlockLoader.SURGERY_TABLE.stateDefinition.possibleStates.filter {
                it.getValue(SurgeryTableBlock.PART) == BedPart.HEAD
            }.toMutableSet(),
            0,
            1
        )
    }
}