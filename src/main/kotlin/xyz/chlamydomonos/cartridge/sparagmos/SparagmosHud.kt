package xyz.chlamydomonos.cartridge.sparagmos

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.util.Mth
import net.minecraft.world.entity.HumanoidArm
import net.neoforged.neoforge.client.gui.GuiLayer
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.inCombatMode

object SparagmosHud : GuiLayer {
    const val BAR_WIDTH = 2
    const val BAR_HEIGHT = 16
    const val BAR_OFFSET = 12


    val centerX get() = Minecraft.getInstance().window.guiScaledWidth / 2
    val centerY get() = Minecraft.getInstance().window.guiScaledHeight / 2

    fun renderBar(guiGraphics: GuiGraphicsExtractor, left: Boolean, value: Int, maxValue: Int) {
        val x = if (left) centerX - BAR_OFFSET - BAR_WIDTH else centerX + BAR_OFFSET
        val y = centerY - BAR_HEIGHT / 2
        val filledHeight = Mth.clamp(value * BAR_HEIGHT / maxValue, 0, BAR_HEIGHT)

        guiGraphics.fill(
            x,
            y,
            x + BAR_WIDTH,
            y + BAR_HEIGHT,
            ColorUtil.rgba(0xffffff, 0x80)
        )

        if (filledHeight > 0) {
            guiGraphics.fill(
                x,
                y + BAR_HEIGHT - filledHeight,
                x + BAR_WIDTH,
                y + BAR_HEIGHT,
                ColorUtil.rgb(0xffffff)
            )
        }
    }

    fun renderPlaceholder(guiGraphics: GuiGraphicsExtractor, left: Boolean) {
        val x = if (left) centerX - BAR_OFFSET - BAR_WIDTH else centerX + BAR_OFFSET
        val y = centerY - BAR_HEIGHT / 2

        guiGraphics.fill(
            x,
            y,
            x + BAR_WIDTH,
            y + BAR_WIDTH,
            ColorUtil.rgba(0xffffff, 0x40)
        )

        guiGraphics.fill(
            x,
            y + BAR_HEIGHT - BAR_WIDTH,
            x + BAR_WIDTH,
            y + BAR_HEIGHT,
            ColorUtil.rgba(0xffffff, 0x40)
        )
    }

    override fun render(guiGraphics: GuiGraphicsExtractor, deltaTracker: DeltaTracker) {
        val player = Minecraft.getInstance().player ?: return
        if (!player.inCombatMode) {
            return
        }

        val hasMainHand = SparagmosInputHandler.hasSparagmos(player, true)
        val hasOffHand = SparagmosInputHandler.hasSparagmos(player, false)
        var hasRightHand: Boolean
        var hasLeftHand: Boolean
        var leftHandCoolDown: Int
        var rightHandCoolDown: Int
        if (player.mainArm == HumanoidArm.LEFT) {
            hasLeftHand = hasMainHand
            hasRightHand = hasOffHand
            leftHandCoolDown = SparagmosInputHandler.mainHandCooldown
            rightHandCoolDown = SparagmosInputHandler.offHandCooldown
        } else {
            hasLeftHand = hasOffHand
            hasRightHand = hasMainHand
            leftHandCoolDown = SparagmosInputHandler.offHandCooldown
            rightHandCoolDown = SparagmosInputHandler.mainHandCooldown
        }

        if (hasLeftHand) {
            renderBar(guiGraphics, true, leftHandCoolDown, SparagmosInputHandler.COOLDOWN)
        } else {
            renderPlaceholder(guiGraphics, true)
        }

        if (hasRightHand) {
            renderBar(guiGraphics, false, rightHandCoolDown, SparagmosInputHandler.COOLDOWN)
        } else {
            renderPlaceholder(guiGraphics, false)
        }
    }
}
