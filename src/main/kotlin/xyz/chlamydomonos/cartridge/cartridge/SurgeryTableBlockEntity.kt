package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import xyz.chlamydomonos.cartridge.loaders.BlockEntityLoader
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import kotlin.jvm.optionals.getOrNull

class SurgeryTableBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(BlockEntityLoader.SURGERY_TABLE, worldPosition, blockState), MenuProvider {
    var playerOn: ServerPlayer? = null
    var playerUsing: ServerPlayer? = null
    var handlingPacket = false

    class InputItemHandler(
        val blockEntity: SurgeryTableBlockEntity? = null
    ) : ItemStacksResourceHandler(1) {
        override fun isValid(index: Int, resource: ItemResource): Boolean {
            if (!(resource.`is`(ItemLoader.CARTRIDGE))) {
                return false
            }

            return resource.get(DataComponentLoader.OPTIONAL_UUID)?.getOrNull() == null
        }
    }

    class OutputItemHandler : ItemStacksResourceHandler(1) {
        override fun isValid(index: Int, resource: ItemResource) = false
    }

    val inputItem = InputItemHandler(this)
    val outputItem = OutputItemHandler()

    override fun getDisplayName() = Component.translatable("block.cartridge.surgery_table")

    override fun createMenu(
        containerId: Int,
        inventory: Inventory,
        player: Player
    ) = SurgeryTableMenu(containerId, inventory, inputItem, outputItem)
}