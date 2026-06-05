package xyz.chlamydomonos.cartridge.cartridge

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.SkinUtil
import xyz.chlamydomonos.cartridge.utils.optionalUUID
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
        val playerHeadPlaceholder = RLUtil.of("textures/gui/player_head_placeholder.png")
        const val TEXTURE_WIDTH = 176
        const val TEXTURE_HEIGHT = 151
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        inventoryLabelY = imageHeight - 94
    }

    fun renderPlayerHead(slotId: Int, graphics: GuiGraphicsExtractor) {
        val itemStack = menu.container.getResource(slotId).toStack()
        if (!(itemStack.`is`(ItemLoader.CARTRIDGE))) {
            return
        }

        val uuid = itemStack.optionalUUID
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2

        if (uuid == null) {
            graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                playerHeadPlaceholder,
                x + 37 + slotId * 18,
                y + 40,
                0f,
                0f,
                12,
                12,
                16,
                16,
                16,
                16
            )
            return
        }

        val skin = SkinUtil.getSkin(uuid, scope)
        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            skin,
            x + 37 + slotId * 18,
            y + 40,
            4f,
            4f,
            12,
            12,
            4,
            4,
            32,
            32
        )
    }

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        renderImageBackground(graphics, background)
        for (id in 0..<6) {
            renderPlayerHead(id, graphics)
        }
    }
}