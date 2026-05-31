package xyz.chlamydomonos.cartridge.combat

import net.minecraft.client.Minecraft
import net.neoforged.neoforge.client.settings.IKeyConflictContext
import xyz.chlamydomonos.cartridge.utils.inCombatMode

object CombatModeContext : IKeyConflictContext {
    override fun isActive(): Boolean {
        val player = Minecraft.getInstance().player ?: return false
        return player.inCombatMode
    }

    override fun conflicts(other: IKeyConflictContext) = this == other
}