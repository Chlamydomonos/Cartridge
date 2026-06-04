package xyz.chlamydomonos.cartridge.sparagmos

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem
import xyz.chlamydomonos.cartridge.combat.CombatModeKeyMappings
import xyz.chlamydomonos.cartridge.utils.ColorUtil
import xyz.chlamydomonos.cartridge.utils.CurioUtil
import xyz.chlamydomonos.cartridge.utils.ModernUIUtil
import java.util.function.Consumer

class SparagmosItem(id: ResourceKey<Item>) : Item(
    Properties()
        .setId(id)
        .stacksTo(1)
), ICurioItem {
    override fun canEquip(context: SlotContext?, stack: ItemStack?): Boolean {
        if (context == null || stack == null || context.identifier != "bracelet") {
            return false
        }

        return CurioUtil.canEquip(context, stack, 2)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun appendHoverText(
        itemStack: ItemStack,
        context: TooltipContext,
        display: TooltipDisplay,
        builder: Consumer<Component>,
        tooltipFlag: TooltipFlag
    ) {
        builder.accept(
            Component
                .translatable(ModernUIUtil.artText("item.cartridge.sparagmos.description"))
                .withColor(ColorUtil.rgb(0xffff60))
        )

        builder.accept(
            Component
                .translatable(
                    "item.cartridge.sparagmos.tooltip",
                    CombatModeKeyMappings.TOGGLE_COMBAT_MODE.translatedKeyMessage
                )
                .withColor(ColorUtil.rgb(0x808080))
        )
    }
}