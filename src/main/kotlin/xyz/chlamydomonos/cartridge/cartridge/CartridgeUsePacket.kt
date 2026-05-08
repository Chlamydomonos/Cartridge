package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.utils.RLUtil

object CartridgeUsePacket : CustomPacketPayload {
    val type = CustomPacketPayload.Type<CartridgeUsePacket>(RLUtil.of("cartridge_use"))
    val codec = StreamCodec.unit<RegistryFriendlyByteBuf, CartridgeUsePacket>(this)
    fun handle(@Suppress("unused") packet: CartridgeUsePacket, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player()
            player.level().playLocalSound(
                player,
                SoundEvents.PLAYER_HURT,
                SoundSource.PLAYERS,
                3f,
                1f
            )

            player.level().playLocalSound(
                player,
                SoundEvents.SLIME_BLOCK_BREAK,
                SoundSource.PLAYERS,
                3f,
                1f
            )
        }
    }

    override fun type() = type
}