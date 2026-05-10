package xyz.chlamydomonos.cartridge.utils

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import net.minecraft.core.UUIDUtil
import java.util.*

object OptionalUUIDCodec : Codec<Optional<UUID>> {
    override fun <T> encode(input: Optional<UUID>, ops: DynamicOps<T>, prefix: T): DataResult<T> {
        val withHasValue = Codec.BOOL.encode(input.isPresent, ops, prefix).result()
        if (withHasValue.isEmpty) {
            throw RuntimeException("WTF")
        }

        return if (input.isPresent) {
            UUIDUtil.CODEC.encode(input.get(), ops, withHasValue.get())
        } else {
            DataResult.success(withHasValue.get())
        }
    }

    override fun <T> decode(ops: DynamicOps<T>, input: T): DataResult<Pair<Optional<UUID>, T>> {
        val hasValueData = Codec.BOOL.decode(ops, input).result()
        if (hasValueData.isEmpty) {
            throw RuntimeException("WTF")
        }
        val hasValue = hasValueData.get().first
        val remainingData = hasValueData.get().second
        if (hasValue) {
            val decodedData = UUIDUtil.CODEC.decode(ops, remainingData).result()
            if (decodedData.isEmpty) {
                throw RuntimeException("WTF")
            }
            val value = decodedData.get().first
            val suffix = decodedData.get().second
            return DataResult.success(Pair.of(Optional.of(value), suffix))
        } else {
            return DataResult.success(Pair.of(Optional.empty(), remainingData))
        }
    }
}