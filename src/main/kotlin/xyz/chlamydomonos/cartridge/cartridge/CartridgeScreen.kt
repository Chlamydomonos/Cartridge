package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.client.network.ClientPacketDistributor

class CartridgeScreen : Screen(Component.empty()) {
    private val suicideButton = Button
        .builder(Component.translatable("gui.cartridge.suicide")) {
            ClientPacketDistributor.sendToServer(SuicidePacket)
            onClose()
        }.size(128, 24).build()

    override fun init() {
        val centerX = width / 2
        val centerY = height / 2
        suicideButton.setPosition(centerX - 64, centerY + 64)
        addRenderableWidget(suicideButton)
    }

    override fun shouldCloseOnEsc() = false
}