package xyz.chlamydomonos.cartridge.mixinimpl.carryon

import net.minecraft.world.entity.player.Player
import tschipp.carryon.common.carry.CarryOnData
import xyz.chlamydomonos.cartridge.utils.carryingHollowUUID

object CarryOnDataManagerMixinImpl {
    fun injectSetCarryData(player: Player, data: CarryOnData) {
        if (data.type == CarryOnData.CarryType.INVALID) {
            player.carryingHollowUUID = null
        }
    }
}