package xyz.chlamydomonos.cartridge.combat

import net.neoforged.neoforge.client.settings.IKeyConflictContext
import net.neoforged.neoforge.client.settings.KeyConflictContext

object ToggleCombatModeContext : IKeyConflictContext {
    override fun isActive() = KeyConflictContext.IN_GAME.isActive

    override fun conflicts(other: IKeyConflictContext) =
        other == this || other == CombatModeContext || other == KeyConflictContext.IN_GAME
}