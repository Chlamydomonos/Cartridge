package xyz.chlamydomonos.catridge

import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.chlamydomonos.catridge.loaders.EffectLoader
import xyz.chlamydomonos.catridge.loaders.PlayerDataLoader

@Mod(Catridge.ID)
object Catridge {
    const val ID = "catridge"

    init {
        EffectLoader.bootstrap(MOD_BUS)
        PlayerDataLoader.bootstrap(MOD_BUS)
    }
}