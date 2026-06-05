package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import net.neoforged.neoforge.transfer.item.ResourceHandlerSlot
import xyz.chlamydomonos.cartridge.loaders.BlockLoader
import xyz.chlamydomonos.cartridge.loaders.MenuLoader

class SurgeryTableMenu(
    containerId: Int,
    playerInventory: Inventory,
    val inputContainer: SurgeryTableBlockEntity.InputItemHandler,
    val outputContainer: SurgeryTableBlockEntity.OutputItemHandler,
    private val packetStatus: DataSlot,
    private val overrideStatus: DataSlot
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

    val levelAccess: ContainerLevelAccess = Unit.run {
        val be = inputContainer.blockEntity ?: return@run ContainerLevelAccess.NULL

        val level = be.level ?: return@run ContainerLevelAccess.NULL

        return@run ContainerLevelAccess.create(level, be.blockPos)
    }

    private val refusedSlot = DataSlot.standalone()
    var refused
        get() = refusedSlot.get() != 0
        set(value) { refusedSlot.set(if (value) 1 else 0) }
    val handlingPacket get() = packetStatus.get() != 0
    val hasOverride get() = overrideStatus.get() != 0

    constructor(id: Int, inv: Inventory) : this(
        id,
        inv,
        SurgeryTableBlockEntity.InputItemHandler(),
        SurgeryTableBlockEntity.OutputItemHandler(),
        DataSlot.standalone(),
        DataSlot.standalone()
    )

    init {
        addSlot(ResourceHandlerSlot(inputContainer, inputContainer::set, 0, 44, 20))
        addSlot(ResourceHandlerSlot(outputContainer, outputContainer::set, 0, 116, 20))
        addStandardInventorySlots(playerInventory, 8, 51)
        addDataSlot(packetStatus)
        addDataSlot(refusedSlot)
    }

    override fun quickMoveStack(
        player: Player,
        slotIndex: Int
    ): ItemStack {
        return ItemStack.EMPTY // TODO
    }

    override fun stillValid(player: Player): Boolean {
        if (!stillValid(levelAccess, player, BlockLoader.SURGERY_TABLE)) {
            return false
        }

        val be = inputContainer.blockEntity ?: return true
        return hasOverride || be.playerOn != null
    }

    override fun removed(player: Player) {
        super.removed(player)

        if (player.level().isClientSide) {
            return
        }

        val be = inputContainer.blockEntity ?: throw RuntimeException("Trying to access null SurgeryTableBlockEntity")
        be.playerUsing = null
        be.handlingPacket = false

        clearContainer(player, inputContainer)
        clearContainer(player, outputContainer)
    }
}
