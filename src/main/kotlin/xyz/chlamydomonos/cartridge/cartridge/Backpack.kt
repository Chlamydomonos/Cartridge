package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.neoforged.neoforge.transfer.item.ItemResource
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler
import xyz.chlamydomonos.cartridge.loaders.DataComponentLoader
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.container

class Backpack(
    val trackingStack: ItemStack? = null
) : ItemStacksResourceHandler(6) {
    private var initializing = true
    init {
        if (trackingStack != null) {
            val container = trackingStack.container ?: throw RuntimeException("Trying to open backpack without item")
            for (i in 0..<container.slots) {
                val stack = container.getStackInSlot(i)
                val resource = ItemResource.of(stack)
                set(i, resource, stack.count)
            }
        }
        initializing = false
    }

    override fun isValid(index: Int, resource: ItemResource): Boolean {
        if (!(resource.`is`(ItemLoader.CARTRIDGE))) {
            return false
        }
        val durability = resource.get(DataComponentLoader.CARTRIDGE_DURABILITY) ?: return false
        val name = resource.get(DataComponentLoader.OPTIONAL_NAME) ?: return false
        return durability > 0 && name.isPresent
    }

    override fun onContentsChanged(index: Int, previousContents: ItemStack) {
        if (trackingStack == null || initializing) {
            return
        }

        trackingStack.container = ItemContainerContents.fromItems(copyToList())
    }
}