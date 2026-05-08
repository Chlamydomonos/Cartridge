package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.renderImageBackground

class SurgeryTableScreen(
    menu: SurgeryTableMenu,
    inventory: Inventory,
    title: Component,
) : AbstractContainerScreen<SurgeryTableMenu>(
    menu,
    inventory,
    title,
    TEXTURE_WIDTH,
    TEXTURE_HEIGHT
) {
    companion object {
        val background = RLUtil.of("textures/gui/surgery_table.png")
        const val TEXTURE_WIDTH = 176
        const val TEXTURE_HEIGHT = 133
    }

    init {
        inventoryLabelY = imageHeight - 94
    }

    private val button = Button
        .Builder(
            Component.translatable("gui.cartridge.create_cartridge"),
            {
                ClientPacketDistributor.sendToServer(CartridgeCreationRequestPacket)
            }
        )
        .size(32, 16)
        .build()

    override fun init() {
        super.init()

        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        button.setPosition(x + 69, y + 20)
        addRenderableWidget(button)
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        renderImageBackground(graphics, background)
    }

    override fun extractContents(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractContents(graphics, mouseX, mouseY, a)
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        if (menu.handlingPacket) {
            graphics.centeredText(
                font,
                Component.translatable("gui.cartridge.pending"),
                x + 69 + 16,
                y + 8,
                ColorUtil.rgb(0x808080)
            )
        } else if (menu.refused) {
            graphics.centeredText(
                font,
                Component.translatable("gui.cartridge.refused"),
                x + 69 + 16,
                y + 8,
                ColorUtil.rgb(0x902020)
            )
        }
    }
}