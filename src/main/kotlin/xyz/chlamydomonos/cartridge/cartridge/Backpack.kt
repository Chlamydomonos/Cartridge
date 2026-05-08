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
    override fun isValid(index: Int, resource: ItemResource): Boolean {
        if (!(resource.`is`(ItemLoader.CARTRIDGE))) {
            return false
        }
        val durability = resource.get(DataComponentLoader.CARTRIDGE_DURABILITY) ?: return false
        val player = resource.get(DataComponentLoader.OPTIONAL_UUID) ?: return false
        return durability > 0 && player.isPresent
    }

    override fun onContentsChanged(index: Int, previousContents: ItemStack) {
        if (trackingStack == null) {
            return
        }

        trackingStack.container = ItemContainerContents.fromItems(copyToList())
    }
}