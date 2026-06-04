package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.ItemContainerContents
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.cartridgeDurability
import xyz.chlamydomonos.cartridge.utils.container

object EjectCartridgePacket : CustomPacketPayload {
    val type = CustomPacketPayload.Type<EjectCartridgePacket>(RLUtil.of("eject_cartridge"))
    val codec = StreamCodec.unit<RegistryFriendlyByteBuf, EjectCartridgePacket>(this)

    fun handle(@Suppress("Unused") packet: EjectCartridgePacket, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player()
            if (player !is ServerPlayer) {
                return@enqueueWork
            }

            val backpack = CartridgeHandler.getBackpack(player) ?: return@enqueueWork
            val container = backpack.container ?: return@enqueueWork
            val stacks = mutableListOf<ItemStack>()
            for (id in 0..<container.slots) {
                stacks.add(container.getStackInSlot(id))
            }

            val toEject = stacks.filter { it.cartridgeDurability == 0 }
            if (toEject.isEmpty()) {
                return@enqueueWork
            }

            for (stack in toEject) {
                val dropped = player.drop(stack, false) ?: continue
                val direction = player.lookAngle
                val throwDirection = Vec3(
                    direction.x,
                    -0.1,
                    direction.z
                ).normalize().scale(-2.0)

                dropped.addDeltaMovement(throwDirection)
            }

            player.playSound(SoundEvents.SNOWBALL_THROW)

            backpack.container = ItemContainerContents.fromItems(
                stacks.filter { it.cartridgeDurability != 0 }
            )
        }
    }

    override fun type() = type
}