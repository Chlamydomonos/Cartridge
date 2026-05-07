package xyz.chlamydomonos.cartridge.cartridge

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext
import xyz.chlamydomonos.cartridge.loaders.datagen.DamageTypeLoader
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.cartridgeStatus

object SuicidePacket : CustomPacketPayload {
    val type = CustomPacketPayload.Type<SuicidePacket>(RLUtil.of("suicide"))
    val codec = StreamCodec.unit<RegistryFriendlyByteBuf, SuicidePacket>(this)

    fun handle(@Suppress("unused") packet: SuicidePacket, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player()
            if (player !is ServerPlayer) {
                return@enqueueWork
            }
            val level = player.level()

            player.hurtServer(
                level,
                level.damageSources().source(DamageTypeLoader.CARTRIDGE),
                Float.MAX_VALUE
            )
            player.cartridgeStatus = CartridgeManager.CartridgeStatus.NONE
        }
    }

    override fun type() = type
}