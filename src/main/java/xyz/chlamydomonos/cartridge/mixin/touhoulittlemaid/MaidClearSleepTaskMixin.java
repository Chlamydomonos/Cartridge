package xyz.chlamydomonos.cartridge.mixin.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.task.MaidClearSleepTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.chlamydomonos.cartridge.mixinimpl.touhoulittlemaid.MaidClearSleepTaskMixinImpl;

@Mixin(MaidClearSleepTask.class)
public class MaidClearSleepTaskMixin {
    @Inject(
        method = "start(Lnet/minecraft/server/level/ServerLevel;Lcom/github/tartaricacid/touhoulittlemaid/entity/passive/EntityMaid;J)V",
        at = @At("HEAD"),
        cancellable = true
    )
    void injectStart(ServerLevel worldIn, EntityMaid entityIn, long gameTimeIn, CallbackInfo ci) {
        MaidClearSleepTaskMixinImpl.INSTANCE.injectStart(entityIn, ci);
    }
}
