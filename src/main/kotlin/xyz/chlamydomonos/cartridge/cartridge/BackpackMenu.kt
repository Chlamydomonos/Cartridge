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

    override fun quickMoveStack(
        player: Player,
        slotIndex: Int
    ): ItemStack {
        return ItemStack.EMPTY // TODO
    }

    override fun stillValid(player: Player): Boolean {
        if (player.level().isClientSide) {
            return true
        }
        return player.mainHandItem == itemStack
    }

}