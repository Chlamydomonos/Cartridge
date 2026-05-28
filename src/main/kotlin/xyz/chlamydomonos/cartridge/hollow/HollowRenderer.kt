package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.constant.DefaultAnimations
import com.geckolib.constant.dataticket.DataTicket
import com.geckolib.renderer.GeoEntityRenderer
import com.geckolib.renderer.base.BoneSnapshots
import com.geckolib.renderer.base.GeoRenderState
import com.geckolib.renderer.base.RenderPassInfo
import com.geckolib.util.ClientUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.world.entity.EntityType
import net.minecraft.world.scores.Team
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.forData

@Suppress("UnstableApiUsage")
class HollowRenderer<R>(
    context: EntityRendererProvider.Context,
    entityType: EntityType<out HollowEntity>
) : GeoEntityRenderer<HollowEntity, R>(
    context,
    HollowModel(entityType)
) where R : EntityRenderState, R : GeoRenderState {
    companion object {
        val BODY_LENGTH = DataTicket.create("body_length", HollowEntityData.BodyLength::class.java)
        val LEFT_ARM_TYPE = DataTicket.create("left_arm_type", HollowEntityData.ArmType::class.java)
        val RIGHT_ARM_TYPE = DataTicket.create("right_arm_type", HollowEntityData.ArmType::class.java)
        val HEAD_OFFSET = DataTicket.create("head_offset", HollowEntityData.Offset::class.java)
        val TAIL_OFFSET = DataTicket.create("tail_offset", HollowEntityData.Offset::class.java)
        val HAS_LEFT_LEG = DataTicket.create("has_left_leg", Boolean::class.java)
        val HAS_RIGHT_LEG = DataTicket.create("has_right_leg", Boolean::class.java)
        val HAS_LARGE_TAIL = DataTicket.create("has_large_tail", Boolean::class.java)
        val LEFT_EAR_TYPE = DataTicket.create("left_ear_type", HollowEntityData.EarType::class.java)
        val RIGHT_EAR_TYPE = DataTicket.create("right_ear_type", HollowEntityData.EarType::class.java)
        val LEFT_EYE_TYPE = DataTicket.create("left_eye_type", HollowEntityData.EyeType::class.java)
        val RIGHT_EYE_TYPE = DataTicket.create("right_eye_type", HollowEntityData.EyeType::class.java)
        val MOUTH_TYPE = DataTicket.create("mouth_type", HollowEntityData.MouthType::class.java)

        val RenderPassInfo<*>.bodyLength get() = forData(BODY_LENGTH)
        val RenderPassInfo<*>.leftArmType get() = forData(LEFT_ARM_TYPE)
        val RenderPassInfo<*>.rightArmType get() = forData(RIGHT_ARM_TYPE)
        val RenderPassInfo<*>.headOffset get() = forData(HEAD_OFFSET)
        val RenderPassInfo<*>.tailOffset get() = forData(TAIL_OFFSET)
        val RenderPassInfo<*>.hasLeftLeg get() = forData(HAS_LEFT_LEG)
        val RenderPassInfo<*>.hasRightLeg get() = forData(HAS_RIGHT_LEG)
        val RenderPassInfo<*>.hasLargeTail get() = forData(HAS_LARGE_TAIL)
        val RenderPassInfo<*>.leftEarType get() = forData(LEFT_EAR_TYPE)
        val RenderPassInfo<*>.rightEarType get() = forData(RIGHT_EAR_TYPE)

        val BoneSnapshots.bodyShort get() = get("body_short").get()
        val BoneSnapshots.bodyMedium get() = get("body_medium").get()
        val BoneSnapshots.bodyLong get() = get("body_long").get()
        val BoneSnapshots.legOffset get() = get("leg_offset").get()
        val BoneSnapshots.leftArmShort get() = get("left_arm_short").get()
        val BoneSnapshots.leftArmMedium get() = get("left_arm_medium").get()
        val BoneSnapshots.leftArmLongUpper get() = get("left_arm_long_upper").get()
        val BoneSnapshots.leftArmLower get() = get("left_arm_lower").get()
        val BoneSnapshots.rightArmShort get() = get("right_arm_short").get()
        val BoneSnapshots.rightArmMedium get() = get("right_arm_medium").get()
        val BoneSnapshots.rightArmLongUpper get() = get("right_arm_long_upper").get()
        val BoneSnapshots.rightArmLower get() = get("right_arm_lower").get()
        val BoneSnapshots.headOffset get() = get("head_offset").get()
        val BoneSnapshots.tailOffset get() = get("tail_offset").get()
        val BoneSnapshots.leftLeg get() = get("left_leg").get()
        val BoneSnapshots.rightLeg get() = get("right_leg").get()
        val BoneSnapshots.tailSmall get() = get("tail_small").get()
        val BoneSnapshots.tailLarge get() = get("tail_large").get()
        val BoneSnapshots.leftEarUp get() = get("left_ear_up").get()
        val BoneSnapshots.leftEarDown get() = get("left_ear_down").get()
        val BoneSnapshots.rightEarUp get() = get("right_ear_up").get()
        val BoneSnapshots.rightEarDown get() = get("right_ear_down").get()
        val BoneSnapshots.leftEarUpInner get() = get("left_ear_up_inner").get()
        val BoneSnapshots.leftEarDownInner get() = get("left_ear_down_inner").get()
        val BoneSnapshots.rightEarUpInner get() = get("right_ear_up_inner").get()
        val BoneSnapshots.rightEarDownInner get() = get("right_ear_down_inner").get()

        fun textureOf(name: String) = RLUtil.of("textures/entity/hollow/${name}.png")
    }

    init {
        renderLayers.addLayer(HollowSkinRenderLayer(this))
        renderLayers.addLayer(
            HollowFaceRenderLayer(this, "left_eye", LEFT_EYE_TYPE,  { false }) {
                when (it) {
                    HollowEntityData.EyeType.NONE -> textureOf("base")
                    HollowEntityData.EyeType.SMALL -> textureOf("eye_small")
                    HollowEntityData.EyeType.DOT -> textureOf("eye_dot")
                    HollowEntityData.EyeType.LARGE -> textureOf("eye_large")
                }
            }
        )

        renderLayers.addLayer(
            HollowFaceRenderLayer(this, "right_eye", RIGHT_EYE_TYPE, { false }) {
                when (it) {
                    HollowEntityData.EyeType.NONE -> textureOf("base")
                    HollowEntityData.EyeType.SMALL -> textureOf("eye_small")
                    HollowEntityData.EyeType.DOT -> textureOf("eye_dot")
                    HollowEntityData.EyeType.LARGE -> textureOf("eye_large")
                }
            }
        )

        renderLayers.addLayer(
            HollowFaceRenderLayer(
                this,
                "mouth",
                MOUTH_TYPE,
                { it != HollowEntityData.MouthType.SPLIT }
            ) {
                when (it) {
                    HollowEntityData.MouthType.NORMAL -> textureOf("mouth_normal")
                    HollowEntityData.MouthType.DOTS -> textureOf("mouth_dots")
                    HollowEntityData.MouthType.SPLIT -> textureOf("mouth_split")
                }
            }
        )

        renderLayers.addLayer(HollowEarRenderLayer(this, "left_ear_up_inner", LEFT_EAR_TYPE, true))
        renderLayers.addLayer(HollowEarRenderLayer(this, "left_ear_down_inner", LEFT_EAR_TYPE, false))
        renderLayers.addLayer(HollowEarRenderLayer(this, "right_ear_up_inner", RIGHT_EAR_TYPE, true))
        renderLayers.addLayer(HollowEarRenderLayer(this, "right_ear_down_inner", RIGHT_EAR_TYPE, false))
    }

    override fun addRenderData(animatable: HollowEntity, relatedObject: Void?, renderState: R, partialTick: Float) {
        renderState.addGeckolibData(BODY_LENGTH, animatable.bodyLength)
        renderState.addGeckolibData(LEFT_ARM_TYPE, animatable.leftArmType)
        renderState.addGeckolibData(RIGHT_ARM_TYPE, animatable.rightArmType)
        renderState.addGeckolibData(HEAD_OFFSET, animatable.headOffset)
        renderState.addGeckolibData(TAIL_OFFSET, animatable.tailOffset)
        renderState.addGeckolibData(HAS_LEFT_LEG, animatable.hasLeftLeg)
        renderState.addGeckolibData(HAS_RIGHT_LEG, animatable.hasRightLeg)
        renderState.addGeckolibData(HAS_LARGE_TAIL, animatable.hasLargeTail)
        renderState.addGeckolibData(LEFT_EAR_TYPE, animatable.leftEarType)
        renderState.addGeckolibData(RIGHT_EAR_TYPE, animatable.rightEarType)
        renderState.addGeckolibData(LEFT_EYE_TYPE, animatable.leftEyeType)
        renderState.addGeckolibData(RIGHT_EYE_TYPE, animatable.rightEyeType)
        renderState.addGeckolibData(MOUTH_TYPE, animatable.mouthType)
    }

    @Suppress("DuplicatedCode")
    override fun adjustModelBonesForRender(info: RenderPassInfo<R>, bones: BoneSnapshots) {
        DefaultAnimations.hardcodedHeadRotation(info, bones, "head_rotation")

        bones.bodyShort.skipRender(info.bodyLength != HollowEntityData.BodyLength.SHORT)
        bones.bodyMedium.skipRender(info.bodyLength != HollowEntityData.BodyLength.MEDIUM)
        bones.bodyLong.skipRender(info.bodyLength != HollowEntityData.BodyLength.LONG)
        bones.legOffset.translateZ = when (info.bodyLength) {
            HollowEntityData.BodyLength.SHORT -> 0f
            HollowEntityData.BodyLength.MEDIUM -> 3f
            HollowEntityData.BodyLength.LONG -> 6f
        }

        bones.leftArmShort.skipRender(info.leftArmType != HollowEntityData.ArmType.SHORT)
        bones.leftArmMedium.skipRender(info.leftArmType != HollowEntityData.ArmType.MEDIUM)
        bones.leftArmLongUpper.skipRender(info.leftArmType != HollowEntityData.ArmType.LONG)
        bones.leftArmLower.skipRender(info.leftArmType != HollowEntityData.ArmType.LONG)

        bones.rightArmShort.skipRender(info.rightArmType != HollowEntityData.ArmType.SHORT)
        bones.rightArmMedium.skipRender(info.rightArmType != HollowEntityData.ArmType.MEDIUM)
        bones.rightArmLongUpper.skipRender(info.rightArmType != HollowEntityData.ArmType.LONG)
        bones.rightArmLower.skipRender(info.rightArmType != HollowEntityData.ArmType.LONG)

        bones.headOffset.translateX = when (info.headOffset) {
            HollowEntityData.Offset.MIDDLE -> 0f
            HollowEntityData.Offset.LEFT -> 2f
            HollowEntityData.Offset.RIGHT -> -2f
        }

        bones.tailOffset.translateX = when (info.tailOffset) {
            HollowEntityData.Offset.MIDDLE -> 0f
            HollowEntityData.Offset.LEFT -> 2f
            HollowEntityData.Offset.RIGHT -> -2f
        }

        bones.leftLeg.skipRender(!info.hasLeftLeg)
        bones.rightLeg.skipRender(!info.hasRightLeg)
        bones.tailSmall.skipRender(info.hasLargeTail)
        bones.tailLarge.skipRender(!info.hasLargeTail)

        bones.leftEarUp.skipRender(info.leftEarType != HollowEntityData.EarType.UP)
        bones.leftEarDown.skipRender(info.leftEarType != HollowEntityData.EarType.DOWN)

        bones.rightEarUp.skipRender(info.rightEarType != HollowEntityData.EarType.UP)
        bones.rightEarDown.skipRender(info.rightEarType != HollowEntityData.EarType.DOWN)

        bones.leftEarUpInner.skipRender(true)
        bones.leftEarDownInner.skipRender(true)
        bones.rightEarUpInner.skipRender(true)
        bones.rightEarDownInner.skipRender(true)
    }

    override fun shouldShowName(animatable: HollowEntity, distToCameraSq: Double): Boolean {
        if (animatable.isDiscrete) {
            val nameRenderCutoff = getNameRenderCutoffDistance(animatable)

            if (distToCameraSq >= nameRenderCutoff * nameRenderCutoff) {
                return false
            }
        }

        if (
            !animatable.shouldShowName() &&
            (!animatable.hasCustomName() || animatable !== this.entityRenderDispatcher.crosshairPickEntity)
        ) {
            return false
        }

        val minecraft = Minecraft.getInstance()
        val player = ClientUtil.getClientPlayer()
        val visibleToClient = player != null && !animatable.isInvisibleTo(player)
        var entityTeam: Team? = animatable.team
        if (entityTeam == null) {
            val teams = animatable.passengers.mapNotNull { it.team }
            if (teams.isNotEmpty()) {
                entityTeam = teams[0]
            }
        }

        if (player == null || entityTeam == null) {
            return Minecraft.renderNames() && animatable !== minecraft.cameraEntity && visibleToClient
        }

        val playerTeam: Team? = ClientUtil.getClientPlayer()!!.team

        return when (entityTeam.nameTagVisibility) {
            Team.Visibility.ALWAYS -> visibleToClient
            Team.Visibility.NEVER -> false
            Team.Visibility.HIDE_FOR_OTHER_TEAMS -> if (playerTeam == null) {
                visibleToClient
            } else {
                entityTeam.isAlliedTo(playerTeam) && (entityTeam.canSeeFriendlyInvisibles() || visibleToClient)
            }

            Team.Visibility.HIDE_FOR_OWN_TEAM -> if (playerTeam == null) {
                visibleToClient
            } else {
                !entityTeam.isAlliedTo(playerTeam) && visibleToClient
            }
        }
    }
}