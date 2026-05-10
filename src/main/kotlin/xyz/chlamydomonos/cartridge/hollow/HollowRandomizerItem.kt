package xyz.chlamydomonos.cartridge.hollow

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import java.util.function.Consumer

class HollowRandomizerItem(id: ResourceKey<Item>) : Item(Properties().setId(id).stacksTo(1)) {
    @Deprecated("Deprecated in Java")
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        builder.accept(
            Component
                .translatable("tooltip.cartridge.only_in_creative")
                .withColor(ColorUtil.rgb(0x802020))
        )

        builder.accept(
            Component
                .translatable("tooltip.cartridge.hollow_randomizer")
                .withColor(ColorUtil.rgb(0x808080))
        )
    }
}