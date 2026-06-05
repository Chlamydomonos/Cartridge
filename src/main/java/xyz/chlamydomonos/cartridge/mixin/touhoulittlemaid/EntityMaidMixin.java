package xyz.chlamydomonos.cartridge.mixin.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.chlamydomonos.cartridge.mixinimpl.touhoulittlemaid.EntityMaidMixinImpl;

@Mixin(EntityMaid.class)
public class EntityMaidMixin {
    @Inject(
        method = "startSleeping",
        at = @At(
            value = "INVOKE",
            target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;setHealth(F)V"
        ),
        cancellable = true
    )
    void injectStartSleeping(BlockPos pos, CallbackInfo ci) {
        EntityMaidMixinImpl.INSTANCE.injectStartSleeping((EntityMaid)(Object)this, ci);
    }
}
