package xyz.chlamydomonos.cartridge.mixinimpl.minecraft

import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlockEntity
import xyz.chlamydomonos.cartridge.loaders.BlockLoader
import xyz.chlamydomonos.cartridge.utils.isCartridge
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

object LivingEntityMixinImpl {
    fun injectGetBedOrientation(self: LivingEntity, context: CallbackInfoReturnable<Direction>) {
        val pos = self.surgeryTablePos ?: return
        val state = self.level().getBlockState(pos)
        if (state.`is`(BlockLoader.SURGERY_TABLE)) {
            context.returnValue = state.getValue(SurgeryTableBlock.FACING)
        }
    }

    fun injectIsInWall(self: LivingEntity, context: CallbackInfoReturnable<Boolean>) {
        if (self is ServerPlayer && self.isCartridge) {
            context.returnValue = false
        }
    }

    fun injectStopSleeping(self: LivingEntity) {
        if (self.level().isClientSide) {
            return
        }

        val pos = self.surgeryTablePos ?: return
        val blockEntity = self.level().getBlockEntity(pos)
        if (blockEntity !is SurgeryTableBlockEntity) {
            return
        }

        blockEntity.overrideCreateCartridge = null
        blockEntity.overrideOnDestroy = null
    }
}