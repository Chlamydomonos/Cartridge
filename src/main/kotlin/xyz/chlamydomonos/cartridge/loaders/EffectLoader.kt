package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffect
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.curse.CurseEffect

object EffectLoader {
    private val registry = DeferredRegister.create(Registries.MOB_EFFECT, Cartridge.ID)

    fun <T : MobEffect> register(name: String, supplier: () -> T): DeferredHolder<MobEffect, T> {
        return registry.register(name, supplier)
    }

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val CURSE = register("curse", ::CurseEffect)
}