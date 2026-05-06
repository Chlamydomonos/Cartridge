package xyz.chlamydomonos.cartridge.mixinimpl

import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import xyz.chlamydomonos.cartridge.cartridge.SurgeryTableBlock
import xyz.chlamydomonos.cartridge.loaders.BlockLoader
import xyz.chlamydomonos.cartridge.utils.surgeryTablePos

object LivingEntityMixinImpl {
    fun injectGetBedOrientation(self: LivingEntity): Direction? {
        if (self !is Player) {
            return null
        }

        val pos = self.surgeryTablePos ?: return null
        val state = self.level().getBlockState(pos)
        if (state.`is`(BlockLoader.SURGERY_TABLE)) {
            return state.getValue(SurgeryTableBlock.FACING)
        }
        return null
    }
}