package xyz.chlamydomonos.cartridge.mixinimpl.carryon

import net.neoforged.fml.loading.FMLLoader
import org.objectweb.asm.tree.ClassNode
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin
import org.spongepowered.asm.mixin.extensibility.IMixinInfo

class CarryOnMixinPlugin : IMixinConfigPlugin {
    override fun onLoad(mixinPackage: String?) {}

    override fun getRefMapperConfig() = null

    override fun shouldApplyMixin(targetClassName: String?, mixinClassName: String?) =
        FMLLoader.getCurrentOrNull()?.loadingModList?.getModFileById("carryon") != null

    override fun acceptTargets(myTargets: Set<String?>?, otherTargets: Set<String?>?) {}

    override fun getMixins() = null

    override fun preApply(tcn: String?, tc: ClassNode?, mcn: String?, mi: IMixinInfo?) {}

    override fun postApply(tcn: String?, tc: ClassNode?, mcn: String?, mi: IMixinInfo?) {}
}