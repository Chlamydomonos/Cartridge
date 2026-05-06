package xyz.chlamydomonos.cartridge.loaders

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import xyz.chlamydomonos.cartridge.Cartridge
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock

object BlockLoader {
    private val registry = DeferredRegister.createBlocks(Cartridge.ID)
    val blocks = mutableListOf<DeferredBlock<out Block>>()

    fun <T : Block> register(
        name: String,
        block: (BlockBehaviour.Properties) -> T,
        base: () -> Block
    ): DeferredBlock<T> {
        val holder = registry.registerBlock(name, block) { ->
            BlockBehaviour.Properties.ofFullCopy(base())
        }
        ItemLoader.registerBlock(holder)
        blocks.add(holder)
        return holder
    }

    fun bootstrap(bus: IEventBus) {
        registry.register(bus)
    }

    val SURGERY_TABLE by register("surgery_table", ::SurgeryTableBlock) { Blocks.IRON_BLOCK }
}