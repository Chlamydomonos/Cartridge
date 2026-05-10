package xyz.chlamydomonos.cartridge.hollow

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.UUIDUtil
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.Mth
import java.util.*
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
        val codec = RecordCodecBuilder.create { 
            it.group(
                UUIDUtil.CODEC
                    .optionalFieldOf("player_uuid")
                    .xmap(Optional<UUID>::getOrNull, Optional<UUID>::ofNullable)
                    .forGetter(HollowEntityData::playerUUID),
                Codec.INT.fieldOf("color").forGetter(HollowEntityData::color),
                BodyLength.codec.fieldOf("body_length").forGetter(HollowEntityData::bodyLength),
                ArmType.codec.fieldOf("left_arm_type").forGetter(HollowEntityData::leftArmType),
                ArmType.codec.fieldOf("right_arm_type").forGetter(HollowEntityData::rightArmType),
                Offset.codec.fieldOf("head_offset").forGetter(HollowEntityData::headOffset),
                Offset.codec.fieldOf("tail_offset").forGetter(HollowEntityData::tailOffset),
                Codec.BOOL.fieldOf("has_left_leg").forGetter(HollowEntityData::hasLeftLeg),
                Codec.BOOL.fieldOf("has_right_leg").forGetter(HollowEntityData::hasRightLeg),
                Codec.BOOL.fieldOf("has_large_tail").forGetter(HollowEntityData::hasLargeTail),
                EarType.codec.fieldOf("left_ear_type").forGetter(HollowEntityData::leftEarType),
                EarType.codec.fieldOf("right_ear_type").forGetter(HollowEntityData::rightEarType),
                EyeType.codec.fieldOf("left_eye_type").forGetter(HollowEntityData::leftEyeType),
                EyeType.codec.fieldOf("right_eye_type").forGetter(HollowEntityData::rightEyeType),
                MouthType.codec.fieldOf("mouth_type").forGetter(HollowEntityData::mouthType)
            ).apply(it, ::HollowEntityData)
        }

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
    }

    constructor() : this(
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