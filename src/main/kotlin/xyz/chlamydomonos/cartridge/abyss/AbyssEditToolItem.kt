package xyz.chlamydomonos.cartridge.abyss

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.item.context.UseOnContext
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.abyssManager
import xyz.chlamydomonos.cartridge.utils.optionalBlockPos
import java.util.*
import java.util.function.Consumer

class AbyssEditToolItem(
    id: ResourceKey<Item>,
    val level: Int,
    val operation: Operation
) : Item(
    Properties()
        .setId(id)
        .component(DataComponentLoader.OPTIONAL_BLOCK_POS, Optional.empty())
) {
    enum class Operation {
        ADD,
        REMOVE
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val data = itemStack.optionalBlockPos
        if (data == null) {
            builder.accept(
                Component
                    .translatable("tooltip.cartridge.abyss_edit.start")
                    .withColor(ColorUtil.rgb(0x808080))
            )
        } else {
            builder.accept(
                Component
                    .translatable("tooltip.cartridge.abyss_edit.end1")
                    .withColor(ColorUtil.rgb(0x808080))
            )
            builder.accept(
                Component
                    .translatable("tooltip.cartridge.abyss_edit.end2")
                    .withColor(ColorUtil.rgb(0x808080))
            )
        }
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS
        }

        val isShift = context.player?.isShiftKeyDown ?: return InteractionResult.PASS
        val stack = context.itemInHand
        val data = stack.optionalBlockPos
        val pos = context.clickedPos
        val level = context.level
        if (data == null) {
            if (!level.isClientSide) {
                stack.optionalBlockPos = pos
            }
            return InteractionResult.SUCCESS
        } else if (isShift) {
            if (!level.isClientSide) {
                stack.optionalBlockPos = null
            }
            return InteractionResult.SUCCESS
        } else {
            val item = stack.item
            if (!level.isClientSide && level is ServerLevel && item is AbyssEditToolItem) {
                val abyssManager = level.abyssManager
                when (item.operation) {
                    Operation.ADD -> abyssManager.setValue(level, data, pos, item.level.toByte())
                    Operation.REMOVE -> abyssManager.replaceValue(level, data, pos, item.level.toByte(), 0)
                }
                stack.optionalBlockPos = null
            }
            return InteractionResult.SUCCESS
        }
    }
}