package xyz.chlamydomonos.cartridge.mixin.carryon;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.PickupHandler;
import xyz.chlamydomonos.cartridge.mixinimpl.carryon.PickupHandlerMixinImpl;

import java.util.function.Function;

@Mixin(PickupHandler.class)
public abstract class PickupHandlerMixin {
    @Inject(
        method = "tryPickupEntity",
        at = @At(
            value = "INVOKE",
            target = "Ltschipp/carryon/common/carry/CarryOnData;setEntity(Lnet/minecraft/world/entity/Entity;)V"
        )
    )
    private static void injectTryPickupEntity(
        ServerPlayer player,
        Entity entity,
        Function<Entity, Boolean> pickupCallback,
        CallbackInfoReturnable<Boolean> cir
    ) {
        PickupHandlerMixinImpl.INSTANCE.injectTryPickupEntity(player, entity);
    }
}
