package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

class CartridgeItem(id: ResourceKey<Item>) : Item(
    Properties().setId(id)
)