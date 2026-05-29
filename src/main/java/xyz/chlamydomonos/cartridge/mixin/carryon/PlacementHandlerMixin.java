package xyz.chlamydomonos.cartridge.mixin.carryon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tschipp.carryon.common.carry.PlacementHandler;
import xyz.chlamydomonos.cartridge.mixinimpl.carryon.PlacementHandlerMixinImpl;

import java.util.function.BiFunction;

@Mixin(PlacementHandler.class)
public abstract class PlacementHandlerMixin {
    @Inject(
        method = "tryPlaceEntity",
        at = @At(
            value = "INVOKE",
            target = "Ltschipp/carryon/common/carry/CarryOnData;clear()V",
            ordinal = 1
        )
    )
    private static void injectTryPlaceEntity(
        ServerPlayer player,
        BlockPos pos,
        Direction facing,
        BiFunction<Vec3, Entity, Boolean> placementCallback,
        CallbackInfoReturnable<Boolean> cir,
        @Local(name = "entity") Entity entity
    ) {
        PlacementHandlerMixinImpl.INSTANCE.injectTryPlaceEntity(player, entity);
    }

    @Inject(
        method = "tryStackEntity",
        at = @At(
            value = "INVOKE",
            target = "Ltschipp/carryon/common/carry/CarryOnData;clear()V"
        )
    )
    private static void injectTryStackEntity(
        ServerPlayer player,
        Entity entityClicked,
        CallbackInfo ci,
        @Local(name = "entityHeld") Entity entityHeld
    ) {
        PlacementHandlerMixinImpl.INSTANCE.injectTryStackEntity(player, entityHeld);
    }
}
