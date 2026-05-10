package xyz.chlamydomonos.cartridge

import com.mojang.logging.LogUtils
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.chlamydomonos.cartridge.loaders.*

@Mod(Cartridge.ID)
object Cartridge {
    const val ID = "cartridge"
    val logger = LogUtils.getLogger()

    init {
        EffectLoader.bootstrap(MOD_BUS)
        EntityDataLoader.bootstrap(MOD_BUS)
        EntityLoader.bootstrap(MOD_BUS)
        ItemLoader.bootstrap(MOD_BUS)
        DataComponentLoader.bootstrap(MOD_BUS)
        BlockLoader.bootstrap(MOD_BUS)
        BlockEntityLoader.bootstrap(MOD_BUS)
        MenuLoader.bootstrap(MOD_BUS)
    }
}