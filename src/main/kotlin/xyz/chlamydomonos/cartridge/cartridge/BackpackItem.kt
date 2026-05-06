package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import top.theillusivec4.curios.api.type.capability.ICurioItem

class BackpackItem(id: ResourceKey<Item>) : Item(
    Properties()
        .setId(id)
        .stacksTo(1)
), ICurioItem