package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot
import xyz.chlamydomonos.cartridge.loaders.MenuLoader

class SurgeryTableMenu(
    containerId: Int,
    playerInventory: Inventory,
    val inputContainer: SurgeryTableBlockEntity.InputItemHandler,
    val outputContainer: SurgeryTableBlockEntity.OutputItemHandler
) : AbstractContainerMenu(MenuLoader.SURGERY_TABLE, containerId) {
    companion object {
        fun clearContainer(player: Player, container: ItemStacksResourceHandler) {
            val stacks = container.copyToList()
            for (stack in stacks) {
                if (stack.isEmpty) {
                    continue
                }
                dropOrPlaceInInventory(player, stack)
            }
        }
    }

    constructor(id: Int, inv: Inventory) : this(
        id,
        inv,
        SurgeryTableBlockEntity.InputItemHandler(),
        SurgeryTableBlockEntity.OutputItemHandler()
    )

    init {
        addSlot(ResourceHandlerSlot(inputContainer, inputContainer::set, 0, 44, 20))
        addSlot(ResourceHandlerSlot(outputContainer, outputContainer::set, 0, 116, 20))
        addStandardInventorySlots(playerInventory, 8, 51)
    }

    override fun quickMoveStack(
        player: Player,
        slotIndex: Int
    ): ItemStack {
        return ItemStack.EMPTY // TODO
    }

    override fun stillValid(player: Player): Boolean {
        return true // TODO
    }

    override fun removed(player: Player) {
        super.removed(player)

        if (player.level().isClientSide) {
            return
        }

        val be = inputContainer.blockEntity ?: throw RuntimeException("Trying to access null SurgeryTableBlockEntity")
        be.playerUsing = null

        clearContainer(player, inputContainer)
        clearContainer(player, outputContainer)
    }
}
