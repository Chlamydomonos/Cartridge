package xyz.chlamydomonos.catridge

import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.chlamydomonos.catridge.loaders.*

@Mod(Cartridge.ID)
object Cartridge {
    const val ID = "cartridge"

    init {
        EffectLoader.bootstrap(MOD_BUS)
        PlayerDataLoader.bootstrap(MOD_BUS)
        EntityLoader.bootstrap(MOD_BUS)
        EntityDataLoader.bootstrap(MOD_BUS)
        ItemLoader.bootstrap(MOD_BUS)
        DataComponentLoader.bootstrap(MOD_BUS)
    }
}