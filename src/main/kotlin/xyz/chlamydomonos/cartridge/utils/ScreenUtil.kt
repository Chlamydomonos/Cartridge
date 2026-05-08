package xyz.chlamydomonos.cartridge.utils

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.resources.Identifier
import net.minecraft.world.inventory.AbstractContainerMenu

fun <T : AbstractContainerMenu> AbstractContainerScreen<T>.renderImageBackground(
    graphics: GuiGraphicsExtractor,
    image: Identifier
) {
    val x = (width - imageWidth) / 2
    val y = (height - imageHeight) / 2
    graphics.blit(
        RenderPipelines.GUI_TEXTURED,
        image,
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