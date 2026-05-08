package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.neoforge.network.PacketDistributor
import top.theillusivec4.curios.api.CuriosApi
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.cartridgeDurability
import xyz.chlamydomonos.cartridge.utils.cartridgeManager
import xyz.chlamydomonos.cartridge.utils.container
import xyz.chlamydomonos.cartridge.utils.optionalUUID
import kotlin.jvm.optionals.getOrNull
import kotlin.math.max

object CartridgeHandler {
    fun tryUseCartridge(player: ServerPlayer, yDiff: Int, curseLevel: Int): Boolean {
        val backpack = getBackpack(player) ?: return false
        val container = backpack.container ?: return false
        val stacks = mutableListOf<ItemStack>()
        for (id in 0..<container.slots) {
            stacks.add(container.getStackInSlot(id))
        }

        var used = false
        for (stack in stacks) {
            if (!(stack.`is`(ItemLoader.CARTRIDGE))) {
                continue
            }

            val uuid = stack.optionalUUID ?: continue
            val durability = stack.cartridgeDurability
            if (durability <= 0) {
                continue
            }

            used = true

            val durabilityLoss = yDiff * (if (curseLevel == 6) 120 else curseLevel)
            val newDurability = max(0, durability - durabilityLoss)
            stack.cartridgeDurability = newDurability
            if (newDurability <= 0) {
                player.level().cartridgeManager.setStatus(uuid, CartridgeManager.CartridgeStatus.DEAD)
            } else {
                val targetPlayer = player.level().server.playerList.getPlayer(uuid) ?: break
                PacketDistributor.sendToPlayer(targetPlayer, CartridgeUsePacket)
            }
            break
        }

        if (used) {
            PacketDistributor.sendToPlayer(player, CartridgeUsePacket)
            backpack.container = ItemContainerContents.fromItems(stacks)
        }

        return used
    }

    fun getBackpack(player: Player): ItemStack? {
        val curiosInventory = CuriosApi.getCuriosInventory(player).getOrNull() ?: return null
        val backpacks = curiosInventory.getStacksHandler("back").getOrNull()?.stacks ?: return null
        for (slotId in 0..<backpacks.slots) {
            val stack = backpacks.getStackInSlot(slotId)
            if (stack.`is`(ItemLoader.BACKPACK)) {
                return stack
            }
        }
        return null
    }

    fun iterateCartridges(backpack: ItemStack, function: (ItemStack) -> Unit) {
        val container = backpack.container ?: return
        for (id in 0..<container.slots) {
            val stack = container.getStackInSlot(id)
            if (stack.`is`(ItemLoader.CARTRIDGE)) {
                function(stack)
            }
        }
    }

    fun onEquip(equipper: ServerPlayer, cartridge: ItemStack) {
        val uuid = cartridge.optionalUUID ?: return
        val durability = cartridge.cartridgeDurability
        if (durability <= 0) {
            return
        }

        val manager = equipper.level().cartridgeManager
        val status = manager.get(uuid).status
        if (status == CartridgeManager.CartridgeStatus.FREE) {
            manager.setStatus(uuid, CartridgeManager.CartridgeStatus.EQUIPPED)
            manager.setEquipper(uuid, equipper.uuid)
        }
    }

    fun onUnequip(equipper: ServerPlayer, cartridge: ItemStack) {
        val uuid = cartridge.optionalUUID ?: return
        val durability = cartridge.cartridgeDurability
        if (durability <= 0) {
            return
        }

        val manager = equipper.level().cartridgeManager
        val status = manager.get(uuid).status
        if (status == CartridgeManager.CartridgeStatus.EQUIPPED) {
            manager.setStatus(uuid, CartridgeManager.CartridgeStatus.FREE)
            manager.setEquipper(uuid, null)
        }
    }
}