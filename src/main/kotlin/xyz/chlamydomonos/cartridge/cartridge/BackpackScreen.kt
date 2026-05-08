package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.renderImageBackground

class BackpackScreen(
    menu: BackpackMenu,
    inventory: Inventory,
    title: Component
) : AbstractContainerScreen<BackpackMenu>(
    menu,
    inventory,
    title,
    TEXTURE_WIDTH,
    TEXTURE_HEIGHT
) {
    companion object {
        val background = RLUtil.of("textures/gui/backpack.png")
        const val TEXTURE_WIDTH = 176
        const val TEXTURE_HEIGHT = 151
    }

    init {
        inventoryLabelY = imageHeight - 94
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        renderImageBackground(graphics, background)
    }
}