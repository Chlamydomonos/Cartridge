package xyz.chlamydomonos.cartridge.utils

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

object PoseUtil {
    const val PX_SIZE = 1.0 / 16

    fun applyModelPartTransform(offset: Vec3, partOffset: Vec3, poseStack: PoseStack, part: ModelPart) {
        poseStack.mulPose(Axis.ZP.rotationDegrees(180f))

        poseStack.translate(
            -part.x * PX_SIZE,
            -part.y * PX_SIZE,
            -part.z * PX_SIZE
        )

        poseStack.mulPose(
            Quaternionf()
                .rotateZYX(
                    -part.zRot,
                    -part.yRot,
                    -part.xRot
                )
        )

        poseStack.translate(offset.reverse())
        poseStack.translate(partOffset.reverse())
    }
}