package xyz.chlamydomonos.cartridge.mixin.carryon;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tschipp.carryon.common.carry.CarryOnData;
import tschipp.carryon.common.carry.CarryOnDataManager;
import xyz.chlamydomonos.cartridge.mixinimpl.carryon.CarryOnDataManagerMixinImpl;

@Mixin(CarryOnDataManager.class)
public abstract class CarryOnDataManagerMixin {
    @Inject(
        method = "setCarryData",
        at = @At("HEAD")
    )
    private static void injectSetCarryData(Player player, CarryOnData data, CallbackInfo ci) {
        CarryOnDataManagerMixinImpl.INSTANCE.injectSetCarryData(player, data);
    }
}
