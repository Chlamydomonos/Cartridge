package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.cartridgeDurability
import xyz.chlamydomonos.cartridge.utils.cartridgeManager
import xyz.chlamydomonos.cartridge.utils.container
import xyz.chlamydomonos.cartridge.utils.optionalUUID

@EventBusSubscriber
object CartridgeTicker {
    fun tryModifyCartridge(cartridge: ItemStack, manager: CartridgeManager): Boolean {
        if (!(cartridge.`is`(ItemLoader.CARTRIDGE))) {
            return false
        }
        val uuid = cartridge.optionalUUID ?: return false
        val status = manager.get(uuid).status
        if (status == CartridgeManager.CartridgeStatus.DEAD || status == CartridgeManager.CartridgeStatus.NONE) {
            val returnValue = cartridge.cartridgeDurability != 0
            cartridge.cartridgeDurability = 0
            return returnValue
        }
        return false
    }

    fun tickBackpack(backpack: ItemStack, manager: CartridgeManager): Boolean {
        val container = backpack.container ?: return false
        val stacks = container.allItemsCopyStream()
        val modifiedStacks = mutableListOf<ItemStack>()
        var hasModified = false
        for (stack in stacks) {
            hasModified = hasModified || tryModifyCartridge(stack, manager)
            modifiedStacks.add(stack)
        }
        if (hasModified) {
            backpack.container = ItemContainerContents.fromItems(modifiedStacks)
        }
        return hasModified
    }

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        if (player !is ServerPlayer) {
            return
        }

        val cartridgeManager = player.level().cartridgeManager

        val backpack = CartridgeHandler.getBackpack(player)

        if (backpack != null) {
            tickBackpack(backpack, cartridgeManager)
        }

        val menu = player.containerMenu
        for (slot in menu.slots) {
            val stack = slot.item
            if (tryModifyCartridge(stack, cartridgeManager)) {
                slot.set(stack)
            } else if (stack.`is`(ItemLoader.BACKPACK)) {
                if (tickBackpack(stack, cartridgeManager)) {
                    slot.set(stack)
                }
            }
        }
    }
}