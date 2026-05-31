package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.core.component.DataComponents
import net.minecraft.resources.ResourceKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.SimpleMenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.level.Level
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.type.capability.ICurioItem

class BackpackItem(id: ResourceKey<Item>) : Item(
    Properties()
        .setId(id)
        .stacksTo(1)
        .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
), ICurioItem {
    override fun canEquip(context: SlotContext?, stack: ItemStack?): Boolean {
        return !(context == null || stack == null || context.identifier != "back")
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.PASS
        }

        if (hand == InteractionHand.MAIN_HAND) {
            val stack = player.mainHandItem

            player.openMenu(
                SimpleMenuProvider(
                    { id, inventory, _ ->
                        BackpackMenu(id, inventory, stack)
                    },
                    stack.itemName
                )
            )

            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }
}