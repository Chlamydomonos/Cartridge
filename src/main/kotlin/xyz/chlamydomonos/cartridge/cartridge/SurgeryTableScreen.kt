package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import xyz.chlamydomonos.cartridge.utils.RLUtil

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
        val BACKGROUND = RLUtil.of("textures/gui/surgery_table.png")
        const val TEXTURE_WIDTH = 176
        const val TEXTURE_HEIGHT = 133
    }

    init {
        inventoryLabelY = imageHeight - 94
    }

    private val button = Button
        .Builder(
            Component.translatable("gui.cartridge.create_cartridge"),
            {}
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

        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            BACKGROUND,
            x,
            y,
            0f,
            0f,
            imageWidth,
            imageHeight,
            256,
            256
        )
    }
}