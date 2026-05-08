package xyz.chlamydomonos.cartridge.mixin;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.widget.ModListWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xyz.chlamydomonos.cartridge.mixinimpl.ModListWidgetModEntryMixinImpl;

@Mixin(ModListWidget.ModEntry.class)
public class ModListWidgetModEntryMixin {
    @Shadow
    @Final
    private ModContainer container;

    @ModifyArg(
            method = "Lnet/neoforged/neoforge/client/gui/widget/ModListWidget$ModEntry;extractContent(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIZF)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/neoforged/neoforge/client/gui/widget/ModListWidget;stripControlCodes(Ljava/lang/String;)Ljava/lang/String;",
                    ordinal = 0
            )
    )
    String modifyArgsExtractContentStripControlCodes(String arg) {
        return ModListWidgetModEntryMixinImpl.INSTANCE.modifyArgsExtractContentStripControlCodes(arg, container);
    }
}
