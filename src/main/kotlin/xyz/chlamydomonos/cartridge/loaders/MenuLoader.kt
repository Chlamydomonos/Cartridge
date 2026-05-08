package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.core.registries.Registries
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.BackpackMenu
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableMenu

object MenuLoader {
    private val registry = DeferredRegister.create(Registries.MENU, Cartridge.ID)

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val SURGERY_TABLE by registry.register("surgery_table") { ->
        MenuType(::SurgeryTableMenu, FeatureFlags.DEFAULT_FLAGS)
    }

    val BACKPACK by registry.register("backpack") { ->
        MenuType(::BackpackMenu, FeatureFlags.DEFAULT_FLAGS)
    }
}