package xyz.chlamydomonos.cartridge.utils

import net.minecraft.client.Minecraft
import xyz.chlamydomonos.cartridge.cartridge.CartridgeCreationConfirmScreen

object ScreenOpenWrapper {
    fun openCartridgeCreationConfirmScreen(name: String) {
        Minecraft.getInstance().setScreen(CartridgeCreationConfirmScreen(name))
    }
}