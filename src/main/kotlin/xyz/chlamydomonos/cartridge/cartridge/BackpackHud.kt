package xyz.chlamydomonos.cartridge.cartridge

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.util.Mth
import net.neoforged.neoforge.client.gui.GuiLayer
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.*
import kotlin.math.max

object BackpackHud : GuiLayer {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    
    override fun render(guiGraphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        val player = Minecraft.getInstance().player ?: return
        val backpack = CartridgeHandler.getBackpack(player) ?: return
        val container = backpack.container ?: return
        val stacks = container.allItemsCopyStream().filter { it.`is`(ItemLoader.CARTRIDGE) }.toList()

        var yOffset = 10
        val xStart = 10

        for (stack in stacks) {
            val uuid = stack.optionalUUID ?: continue
            val skin = SkinUtil.getSkin(uuid, scope)
            val durability = stack.cartridgeDurability

            guiGraphics.blit(
                net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                skin,
                xStart,
                yOffset,
                4f,
                4f,
                12,
                12,
                4,
                4,
                32,
                32,
                if (durability == 0) {
                    ColorUtil.rgbAsInt(0x404040)
                } else {
                    ColorUtil.rgbAsInt(0xffffff)
                }
            )

            if (durability > 0) {
                val barWidth = Mth.clamp((durability * 14f / CartridgeItem.MAX_DURABILITY).toInt(), 0, 14)
                val healthPercentage = max(0f, durability.toFloat() / CartridgeItem.MAX_DURABILITY)
                val color = ColorUtil.rgbAsInt(Mth.hsvToRgb(healthPercentage / 3f, 1f, 1f))

                guiGraphics.fill(
                    xStart - 1,
                    yOffset + 13,
                    xStart + 13,
                    yOffset + 15,
                    ColorUtil.rgbAsInt(0)
                )
                guiGraphics.fill(
                    xStart - 1,
                    yOffset + 13,
                    xStart + barWidth,
                    yOffset + 14,
                    color
                )
            }

            yOffset += 18
        }
    }
}