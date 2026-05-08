package xyz.chlamydomonos.cartridge.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ModListScreen;
import net.neoforged.neoforgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.chlamydomonos.cartridge.mixinimpl.ModListScreenMixinImpl;

@Mixin(ModListScreen.class)
public abstract class ModListScreenMixin {
    @ModifyArg(
            method = "Lnet/neoforged/neoforge/client/gui/ModListScreen;updateCache()V",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
                    ordinal = 0
            )
    )
    Object modifyArgUpdateCacheLinesAdd(
            Object arg,
            @Local(name = "selectedMod") IModInfo selectedMod
    ) {
        return ModListScreenMixinImpl.INSTANCE.modifyArgUpdateCacheLinesAdd((String) arg, selectedMod);
    }

    @ModifyArg(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Font;width(Ljava/lang/String;)I",
                    ordinal = 0
            )
    )
    String modifyArgInit(
            String arg,
            @Local(name = "mod") ModContainer mod
    ) {
        return ModListScreenMixinImpl.INSTANCE.modifyArgInit(arg, mod);
    }
}
