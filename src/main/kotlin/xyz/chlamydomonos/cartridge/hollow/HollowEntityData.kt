package xyz.chlamydomonos.cartridge.hollow

import com.mojang.serialization.*
import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.Mth
import xyz.chlamydomonos.cartridge.utils.OptionalUUIDCodec
import java.util.*
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull
import kotlin.random.Random

data class HollowEntityData(
    var playerUUID: UUID?,
    var color: Int,
    var bodyLength: BodyLength,
    var leftArmType: ArmType,
    var rightArmType: ArmType,
    var headOffset: Offset,
    var tailOffset: Offset,
    var hasLeftLeg: Boolean,
    var hasRightLeg: Boolean,
    var hasLargeTail: Boolean,
    var leftEarType: EarType,
    var rightEarType: EarType,
    var leftEyeType: EyeType,
    var rightEyeType: EyeType,
    var mouthType: MouthType
) {
    
    enum class BodyLength {
        SHORT,
        MEDIUM,
        LONG;

        companion object {
            val codec = Codec.BYTE.xmap(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
            
            val streamCodec = ByteBufCodecs.BYTE.map(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
            
            fun random(random: Random) = entries[random.nextInt(3)]
        }
    }

    enum class ArmType {
        NONE,
        SHORT,
        MEDIUM,
        LONG;

        companion object {
            val codec = Codec.BYTE.xmap(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
            
            val streamCodec = ByteBufCodecs.BYTE.map(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
        }
    }

    enum class EarType {
        NONE,
        UP,
        DOWN;

        companion object {
            val codec = Codec.BYTE.xmap(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )

            val streamCodec = ByteBufCodecs.BYTE.map(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )

            fun random(random: Random) = entries[random.nextInt(3)]
        }
    }

    enum class Offset {
        MIDDLE,
        LEFT,
        RIGHT;

        companion object {
            val codec = Codec.BYTE.xmap(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )

            val streamCodec = ByteBufCodecs.BYTE.map(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
        }

    }

    enum class EyeType {
        NONE,
        SMALL,
        DOT,
        LARGE;

        companion object {
            val codec = Codec.BYTE.xmap(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )

            val streamCodec = ByteBufCodecs.BYTE.map(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )
        }

    }

    enum class MouthType {
        NORMAL,
        DOTS,
        SPLIT;

        companion object {
            val codec = Codec.BYTE.xmap(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )

            val streamCodec = ByteBufCodecs.BYTE.map(
                { entries[it.toInt()] },
                { it.ordinal.toByte() }
            )

            fun random(random: Random) = entries[random.nextInt(3)]
        }
    }
    
    companion object {
        private val mapCodec = object : MapCodec<HollowEntityData>() {
            override fun <T> keys(ops: DynamicOps<T>) = Stream.of(
                ops.createString("player_uuid"),
                ops.createString("color"),
                ops.createString("body_length"),
                ops.createString("left_arm_type"),
                ops.createString("right_arm_type"),
                ops.createString("head_offset"),
                ops.createString("tail_offset"),
                ops.createString("has_left_leg"),
                ops.createString("has_right_leg"),
                ops.createString("has_large_tail"),
                ops.createString("left_ear_type"),
                ops.createString("right_ear_type"),
                ops.createString("left_eye_type"),
                ops.createString("right_eye_type"),
                ops.createString("mouth_type")
            )

            override fun <T> decode(ops: DynamicOps<T>, input: MapLike<T>): DataResult<HollowEntityData> {
                val uuid = OptionalUUIDCodec.parse(ops, input.get("player_uuid")).result().getOrNull()?.getOrNull()
                return DataResult.success(
                    HollowEntityData(
                        uuid,
                        Codec.INT.parse(ops, input.get("color")).result().getOrNull() ?: 0,
                        BodyLength.codec.parse(ops, input.get("body_length")).result().getOrNull() ?: BodyLength.MEDIUM,
                        ArmType.codec.parse(ops, input.get("left_arm_type")).result().getOrNull() ?: ArmType.MEDIUM,
                        ArmType.codec.parse(ops, input.get("right_arm_type")).result().getOrNull() ?: ArmType.MEDIUM,
                        Offset.codec.parse(ops, input.get("head_offset")).result().getOrNull() ?: Offset.MIDDLE,
                        Offset.codec.parse(ops, input.get("tail_offset")).result().getOrNull() ?: Offset.MIDDLE,
                        Codec.BOOL.parse(ops, input.get("has_left_leg")).result().getOrNull() ?: true,
                        Codec.BOOL.parse(ops, input.get("has_right_leg")).result().getOrNull() ?: true,
                        Codec.BOOL.parse(ops, input.get("has_large_tail")).result().getOrNull() ?: false,
                        EarType.codec.parse(ops, input.get("left_ear_type")).result().getOrNull() ?: EarType.UP,
                        EarType.codec.parse(ops, input.get("right_ear_type")).result().getOrNull() ?: EarType.UP,
                        EyeType.codec.parse(ops, input.get("left_eye_type")).result().getOrNull() ?: EyeType.SMALL,
                        EyeType.codec.parse(ops, input.get("right_eye_type")).result().getOrNull() ?: EyeType.SMALL,
                        MouthType.codec.parse(ops, input.get("mouth_type")).result().getOrNull() ?: MouthType.NORMAL
                    )
                )
            }

            override fun <T> encode(input: HollowEntityData, ops: DynamicOps<T>, prefix: RecordBuilder<T>): RecordBuilder<T> {
                val uuidData = OptionalUUIDCodec.encodeStart(ops, Optional.ofNullable(input.playerUUID))
                return prefix
                    .add("player_uuid", uuidData)
                    .add("color", Codec.INT.encodeStart(ops, input.color))
                    .add("body_length", BodyLength.codec.encodeStart(ops, input.bodyLength))
                    .add("left_arm_type", ArmType.codec.encodeStart(ops, input.leftArmType))
                    .add("right_arm_type", ArmType.codec.encodeStart(ops, input.rightArmType))
                    .add("head_offset", Offset.codec.encodeStart(ops, input.headOffset))
                    .add("tail_offset", Offset.codec.encodeStart(ops, input.tailOffset))
                    .add("has_left_leg", Codec.BOOL.encodeStart(ops, input.hasLeftLeg))
                    .add("has_right_leg", Codec.BOOL.encodeStart(ops, input.hasRightLeg))
                    .add("has_large_tail", Codec.BOOL.encodeStart(ops, input.hasLargeTail))
                    .add("left_ear_type", EarType.codec.encodeStart(ops, input.leftEarType))
                    .add("right_ear_type", EarType.codec.encodeStart(ops, input.rightEarType))
                    .add("left_eye_type", EyeType.codec.encodeStart(ops, input.leftEyeType))
                    .add("right_eye_type", EyeType.codec.encodeStart(ops, input.rightEyeType))
                    .add("mouth_type", MouthType.codec.encodeStart(ops, input.mouthType))
            }

        }

        val codec = mapCodec.codec()

        val streamCodec = object : StreamCodec<ByteBuf, HollowEntityData> {
            override fun encode(buf: ByteBuf, data: HollowEntityData) {
                ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC).encode(buf, Optional.ofNullable(data.playerUUID))
                ByteBufCodecs.INT.encode(buf, data.color)
                BodyLength.streamCodec.encode(buf, data.bodyLength)
                ArmType.streamCodec.encode(buf, data.leftArmType)
                ArmType.streamCodec.encode(buf, data.rightArmType)
                Offset.streamCodec.encode(buf, data.headOffset)
                Offset.streamCodec.encode(buf, data.tailOffset)
                ByteBufCodecs.BOOL.encode(buf, data.hasLeftLeg)
                ByteBufCodecs.BOOL.encode(buf, data.hasRightLeg)
                ByteBufCodecs.BOOL.encode(buf, data.hasLargeTail)
                EarType.streamCodec.encode(buf, data.leftEarType)
                EarType.streamCodec.encode(buf, data.rightEarType)
                EyeType.streamCodec.encode(buf, data.leftEyeType)
                EyeType.streamCodec.encode(buf, data.rightEyeType)
                MouthType.streamCodec.encode(buf, data.mouthType)
            }

            override fun decode(buf: ByteBuf): HollowEntityData {
                return HollowEntityData(
                    ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC).decode(buf).getOrNull(),
                    ByteBufCodecs.INT.decode(buf),
                    BodyLength.streamCodec.decode(buf),
                    ArmType.streamCodec.decode(buf),
                    ArmType.streamCodec.decode(buf),
                    Offset.streamCodec.decode(buf),
                    Offset.streamCodec.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    ByteBufCodecs.BOOL.decode(buf),
                    EarType.streamCodec.decode(buf),
                    EarType.streamCodec.decode(buf),
                    EyeType.streamCodec.decode(buf),
                    EyeType.streamCodec.decode(buf),
                    MouthType.streamCodec.decode(buf)
                )
            }

        }

        const val DEFAULT_SATURATION = 0.3f
        const val DEFAULT_BRIGHTNESS = 1f

        fun default() = HollowEntityData(
            null,
            Mth.hsvToRgb(0f, DEFAULT_SATURATION, DEFAULT_BRIGHTNESS),
            BodyLength.MEDIUM,
            ArmType.MEDIUM,
            ArmType.MEDIUM,
            Offset.MIDDLE,
            Offset.MIDDLE,
            true,
            true,
            false,
            EarType.UP,
            EarType.UP,
            EyeType.SMALL,
            EyeType.SMALL,
            MouthType.NORMAL
        )
    }
}