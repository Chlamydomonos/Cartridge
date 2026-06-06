package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.DataSlot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import xyz.chlamydomonos.cartridge.loaders.BlockEntityLoader
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.cartridge.utils.*
import kotlin.jvm.optionals.getOrNull

class SurgeryTableBlockEntity(
    worldPosition: BlockPos,
    blockState: BlockState
) : BlockEntity(BlockEntityLoader.SURGERY_TABLE, worldPosition, blockState), MenuProvider {
    var playerOn: ServerPlayer? = null
    var playerUsing: ServerPlayer? = null
    var handlingPacket = false

    var overrideCreateCartridge: (SurgeryTableBlockEntity.() -> Unit)? = null
    var overrideOnDestroy: (SurgeryTableBlockEntity.() -> Unit)? = null

    var justUsed = false

    class InputItemHandler(
        val blockEntity: SurgeryTableBlockEntity? = null
    ) : ItemStacksResourceHandler(1) {
        override fun isValid(index: Int, resource: ItemResource): Boolean {
            if (!(resource.`is`(ItemLoader.CARTRIDGE))) {
                return false
            }

            return resource.get(DataComponentLoader.OPTIONAL_NAME)?.getOrNull() == null
        }

        override fun onContentsChanged(index: Int, previousContents: ItemStack) {
            blockEntity?.handlingPacket = false
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
    ) = SurgeryTableMenu(
        containerId,
        inventory,
        inputItem,
        outputItem,
        object : DataSlot() {
            override fun get() = if (handlingPacket) 1 else 0
            override fun set(p0: Int) {}
        },
        object : DataSlot() {
            override fun get() = if (overrideCreateCartridge == null) 0 else 1
            override fun set(p0: Int) {}
        },
        object : DataSlot() {
            override fun get() = if (justUsed) 1 else 0
            override fun set(p0: Int) {}
        }
    )

    fun createCartridge() {
        val target = playerOn
        val user = playerUsing
        if (user == null || target == null) {
            throw RuntimeException("Trying to create cartridge without players")
        }

        val level = target.level()
        target.dropAllDeathLoot(level, level.damageSources().source(DamageTypeLoader.CARTRIDGE, user))
        target.cartridgeStatus = CartridgeManager.CartridgeStatus.FREE
        target.teleportTo(
            level.cartridgeDimension,
            level.random.nextDouble() * 100,
            100.0,
            level.random.nextDouble() * 100,
            setOf(),
            target.yRot,
            target.xRot,
            false
        )

        inputItem.set(0, ItemResource.of(ItemStack.EMPTY), 0)
        val outputStack = ItemStack(ItemLoader.CARTRIDGE, 1)
        outputStack.optionalUUID = target.uuid
        outputStack.optionalName = target.plainTextName
        outputItem.set(0, ItemResource.of(outputStack), 1)
        justUsed = true
    }

    override fun preRemoveSideEffects(pos: BlockPos, state: BlockState) {
        if (level?.isClientSide ?: true) {
            return
        }

        overrideOnDestroy?.let {
            it(this)
        }

        val player = playerOn
        if (player is ServerPlayer) {
            player.surgeryTablePos = null
            player.stopSleeping()
        }
    }
}