package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot
import xyz.chlamydomonos.cartridge.loaders.MenuLoader

class BackpackMenu(
    containerId: Int,
    playerInventory: Inventory,
    val itemStack: ItemStack? = null
) : AbstractContainerMenu(MenuLoader.BACKPACK, containerId) {
    val container = Backpack(itemStack)

    init {
        for (id in 0..<6) {
            addSlot(
                ResourceHandlerSlot(
                    container,
                    container::set,
                    id,
                    35 + id * 18,
                    20
                )
            )
        }

        addStandardInventorySlots(playerInventory, 8, 69)
    }

    // AI生成
    override fun quickMoveStack(
        player: Player,
        slotIndex: Int
    ): ItemStack {
        var stack = ItemStack.EMPTY
        val slot = slots[slotIndex]
        if (slot.hasItem()) {
            val stack1 = slot.item
            stack = stack1.copy()
            if (slotIndex < 6) {
                if (!moveItemStackTo(stack1, 6, 42, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!moveItemStackTo(stack1, 0, 6, false)) {
                return ItemStack.EMPTY
            }

            if (stack1.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (stack1.count == stack.count) {
                return ItemStack.EMPTY
            }
            
            slot.onTake(player, stack1)
        }
        return stack
    }

    override fun stillValid(player: Player): Boolean {
        if (player.level().isClientSide) {
            return true
        }
        return player.mainHandItem == itemStack
    }

}