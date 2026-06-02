package xyz.chlamydomonos.cartridge.utils

import net.minecraft.world.item.ItemStack
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.SlotContext
import kotlin.jvm.optionals.getOrNull

object CurioUtil {
    fun canEquip(context: SlotContext, stack: ItemStack, maxCount: Int): Boolean {
        val identifier = context.identifier()
        val index = context.index

        val inventory = CuriosApi.getCuriosInventory(context.entity).getOrNull() ?: return false
        @Suppress("removal", "DEPRECATION")
        var equippedCount = inventory.equippedCurios.let {
            val list = mutableListOf<ItemStack>()
            for (i in 0..<it.slots) {
                list.add(it.getStackInSlot(i))
            }
            list
        }.filter { it.`is`(stack.item) }.size

        val handler = inventory.getStacksHandler(identifier).getOrNull() ?: return false
        val equipped = handler.stacks.getStackInSlot(index) == stack
        if (equipped) {
            equippedCount--
        }

        return equippedCount < maxCount
    }
}