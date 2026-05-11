package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.CombatEntry
import net.minecraft.world.damagesource.DeathMessageType
import net.minecraft.world.entity.LivingEntity
import net.neoforged.fml.common.asm.enumextension.EnumProxy
import net.neoforged.neoforge.common.damagesource.IDeathMessageProvider

object CartridgeDeathMessageProvider : IDeathMessageProvider {
    override fun getDeathMessage(
        entity: LivingEntity,
        lastEntry: CombatEntry,
        mostSignificantFall: CombatEntry?
    ): Component {
        val causer = lastEntry.source.entity
        return if (causer == null) {
            Component.translatable(
                "death.attack.cartridge.cartridge",
                entity.displayName
            )
        } else {
            Component.translatable(
                "death.attack.cartridge.cartridge.player",
                entity.displayName,
                causer.displayName
            )
        }
    }

    val DEATH_MESSAGE_TYPE_PROXY = EnumProxy(DeathMessageType::class.java, "cartridge:cartridge", this)

    val DEATH_MESSAGE_TYPE get() = DEATH_MESSAGE_TYPE_PROXY.value
}