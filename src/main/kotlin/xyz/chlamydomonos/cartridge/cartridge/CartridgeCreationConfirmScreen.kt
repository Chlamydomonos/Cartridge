package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import xyz.chlamydomonos.cartridge.utils.ColorUtil

class CartridgeCreationConfirmScreen(val name: String) : Screen(Component.empty()) {
    private var accept = false

    private val acceptButton = Button.builder(Component.translatable("gui.cartridge.accept")) {
        accept = true
        onClose()
    }.size(64, 24).build()
    private val refuseButton = Button.builder(Component.translatable("gui.cartridge.refuse")) {
        accept = false
        onClose()
    }.size(64, 24).size(64, 24).build()

    override fun init() {
        val centerX = width / 2
        val centerY = height / 2
        acceptButton.setPosition(centerX - 96 - acceptButton.width, centerY + 32)
        refuseButton.setPosition(centerX + 96, centerY + 32)
        addRenderableWidget(acceptButton)
        addRenderableWidget(refuseButton)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractRenderState(graphics, mouseX, mouseY, a)

        val centerX = width / 2
        val centerY = height / 2

        graphics.centeredText(
            font,
            Component.translatable("gui.cartridge.creation_confirm", name),
            centerX,
            centerY - 32,
            ColorUtil.rgbAsInt(0xffffff)
        )
    }

    override fun onClose() {
        ClientPacketDistributor.sendToServer(CartridgeConfirmPacket(accept))
        super.onClose()
    }
}