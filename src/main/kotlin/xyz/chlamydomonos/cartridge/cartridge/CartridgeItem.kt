package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.util.Mth
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import xyz.chlamydomonos.cartridge.utils.cartridgeDurability
import xyz.chlamydomonos.cartridge.utils.optionalName
import xyz.chlamydomonos.cartridge.utils.optionalUUID
import java.util.*
import java.util.function.Consumer
import kotlin.math.max
import kotlin.math.roundToInt

class CartridgeItem(id: ResourceKey<Item>) : Item(
    Properties()
        .setId(id)
        .stacksTo(1)
        .component(DataComponentLoader.OPTIONAL_UUID, Optional.empty())
        .component(DataComponentLoader.OPTIONAL_NAME, Optional.empty())
        .component(DataComponentLoader.CARTRIDGE_DURABILITY, MAX_DURABILITY)
) {
    companion object {
        const val MAX_DURABILITY = 2000
    }

    override fun getName(itemStack: ItemStack): Component {
        val name = itemStack.optionalName
        return if (name == null) {
            super.getName(itemStack)
        } else if (itemStack.cartridgeDurability <= 0) {
            Component.translatable("item.cartridge.cartridge.dead", name)
        } else {
            Component.literal(name)
        }
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        itemStack.optionalUUID ?: return
        val durability = itemStack.cartridgeDurability
        if (durability > 0) {
            builder.accept(Component.translatable("tooltip.cartridge.cartridge.durability", durability))
        }
    }

    override fun isBarVisible(stack: ItemStack): Boolean {
        return stack.cartridgeDurability in 1..<MAX_DURABILITY
    }

    override fun getBarWidth(stack: ItemStack): Int {
        return Mth.clamp((stack.cartridgeDurability * 13.0f / MAX_DURABILITY).roundToInt(), 0, 13)
    }

    override fun getBarColor(stack: ItemStack): Int {
        val healthPercentage = max(0.0f, stack.cartridgeDurability.toFloat() / MAX_DURABILITY)
        return Mth.hsvToRgb(healthPercentage / 3.0f, 1.0f, 1.0f)
    }
}