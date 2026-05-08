package xyz.chlamydomonos.cartridge.utils

import net.minecraft.client.Minecraft
import xyz.chlamydomonos.cartridge.cartridge.CartridgeCreationConfirmScreen
import xyz.chlamydomonos.cartridge.cartridge.CartridgeScreen

object ScreenOpenWrapper {
    fun openCartridgeCreationConfirmScreen(name: String) {
        Minecraft.getInstance().setScreen(CartridgeCreationConfirmScreen(name))
    }

    fun openCartridgeScreen() {
        Minecraft.getInstance().setScreen(CartridgeScreen())
    }
}