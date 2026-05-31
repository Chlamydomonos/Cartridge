package xyz.chlamydomonos.cartridge.utils

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

object PoseUtil {
    const val PX_SIZE = 1.0 / 16

    class Profile(
        modelCenter: Vec3,
        partPos: Vec3,
        partSize: Vec3,
        val extraRotation: Quaternionf = Quaternionf()
    ) {
        val modelCenterMeters = modelCenter.scale(PX_SIZE)
        val partCenterMeters = partPos.add(partSize.scale(0.5)).scale(PX_SIZE)
    }

    fun applyModelPartTransform(
        poseStack: PoseStack,
        part: ModelPart,
        profile: Profile
    ) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f))

        poseStack.translate(
            -part.x * PX_SIZE,
            -part.y * PX_SIZE,
            -part.z * PX_SIZE
        )

        poseStack.mulPose(Quaternionf().rotateZYX(
            part.zRot,
            -part.yRot,
            -part.xRot
        ))

        poseStack.translate(profile.partCenterMeters.reverse())
        poseStack.translate(profile.modelCenterMeters.reverse())

        poseStack.mulPose(profile.extraRotation)
    }
}