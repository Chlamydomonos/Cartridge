package xyz.chlamydomonos.cartridge.mixin.carryon;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.client.render.CarriedObjectRender;
import xyz.chlamydomonos.cartridge.mixinimpl.carryon.CarriedObjectRenderMixinImpl;

@Mixin(CarriedObjectRender.class)
public abstract class CarriedObjectRenderMixin {
    @Inject(
        method = "drawEntity",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void injectDrawEntity(
        Player player,
        PoseStack matrix,
        int light,
        float partialTicks,
        SubmitNodeCollector nodeCollector,
        boolean firstPerson,
        CallbackInfo ci
    ) {
        CarriedObjectRenderMixinImpl.INSTANCE.injectDrawEntity(player, ci);
    }
}
