package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.util.Mth
import net.neoforged.neoforge.client.gui.GuiLayer
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.inCombatMode

object GangwayHud : GuiLayer {
    const val BAR_WIDTH = 16
    const val BAR_HEIGHT = 2
    const val BAR_OFFSET = 12

    val centerX get() = Minecraft.getInstance().window.guiScaledWidth / 2
    val centerY get() = Minecraft.getInstance().window.guiScaledHeight / 2

    fun renderBar(guiGraphics: GuiGraphicsExtractor, value: Int, maxValue: Int, color: Int) {
        val x = centerX - BAR_WIDTH / 2
        val y = centerY - BAR_OFFSET - BAR_HEIGHT
        val filledWidth = Mth.clamp(value * BAR_WIDTH / maxValue, 0, BAR_WIDTH)

        guiGraphics.fill(
            x,
            y,
            x + BAR_WIDTH,
            y + BAR_HEIGHT,
            ColorUtil.rgba(0xffffff, 0x80)
        )

        if (filledWidth > 0) {
            guiGraphics.fill(
                x,
                y,
                x + filledWidth,
                y + BAR_HEIGHT,
                color
            )
        }
    }

    fun renderPlaceholder(guiGraphics: GuiGraphicsExtractor) {
        val x = centerX - BAR_WIDTH / 2
        val y = centerY - BAR_OFFSET - BAR_HEIGHT

        guiGraphics.fill(
            x,
            y,
            x + BAR_HEIGHT,
            y + BAR_HEIGHT,
            ColorUtil.rgba(0xffffff, 0x40)
        )

        guiGraphics.fill(
            x + BAR_WIDTH - BAR_HEIGHT,
            y,
            x + BAR_WIDTH,
            y + BAR_HEIGHT,
            ColorUtil.rgba(0xffffff, 0x40)
        )
    }

    override fun render(
        guiGraphics: GuiGraphicsExtractor,
        deltaTracker: DeltaTracker
    ) {
        val player = Minecraft.getInstance().player ?: return
        if (!player.inCombatMode) {
            return
        }

        if (!GangwayInputHandler.hasGangway(player)) {
            renderPlaceholder(guiGraphics)
            return
        }

        if (GangwayInputHandler.cooldown > 0) {
            renderBar(
                guiGraphics,
                GangwayInputHandler.cooldown,
                GangwayInputHandler.HEAVY_ATTACK_COOLDOWN,
                ColorUtil.rgb(0xff0000)
            )
            return
        }

        renderBar(
            guiGraphics,
            GangwayInputHandler.holdTime,
            GangwayInputHandler.HEAVY_ATTACK_THRESHOLD,
            ColorUtil.rgb(0xffffff)
        )
    }
}
