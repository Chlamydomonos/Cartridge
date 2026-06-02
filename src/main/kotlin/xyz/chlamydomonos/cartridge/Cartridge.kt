package xyz.chlamydomonos.cartridge

import com.mojang.logging.LogUtils
import net.neoforged.fml.common.Mod
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import xyz.chlamydomonos.cartridge.loaders.*

@Mod(Cartridge.ID)
object Cartridge {
    const val ID = "cartridge"
    val logger = LogUtils.getLogger()
    val version = LOADING_CONTEXT.activeContainer.modInfo.version

    init {
        EffectLoader.bootstrap(MOD_BUS)
        DataAttachmentLoader.bootstrap(MOD_BUS)
        EntityLoader.bootstrap(MOD_BUS)
        ItemLoader.bootstrap(MOD_BUS)
        DataComponentLoader.bootstrap(MOD_BUS)
        BlockLoader.bootstrap(MOD_BUS)
        BlockEntityLoader.bootstrap(MOD_BUS)
        MenuLoader.bootstrap(MOD_BUS)
    }
}