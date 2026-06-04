package xyz.chlamydomonos.cartridge.utils

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.renderer.texture.OverlayTexture
import org.joml.Quaternionf
import org.joml.Vector3f
import org.joml.Vector3fc

object BeamRenderer {
    fun renderBeam(
        consumer: VertexConsumer,
        poseStack: PoseStack,
        x: Float,
        y: Float,
        z: Float,
        pitch: Float,
        yaw: Float,
        length: Float,
        thickness: Float,
        vOffset: Float
    ) {
        poseStack.pushPose()

        poseStack.translate(x, y, z)
        poseStack.mulPose(Axis.YP.rotationDegrees(180f - yaw))
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch))

        renderCuboid(consumer, poseStack.last(), length, thickness, vOffset)

        poseStack.popPose()
    }

    fun renderBeam(
        consumer: VertexConsumer,
        poseStack: PoseStack,
        from: Vector3fc,
        to: Vector3fc,
        thickness: Float,
        vOffset: Float
    ) {
        val direction = Vector3f(to).sub(from)
        val length = direction.length()
        poseStack.pushPose()

        poseStack.translate(from.x(), from.y(), from.z())
        if (length > 0f) {
            poseStack.mulPose(Quaternionf().rotationTo(Vector3f(0f, 0f, -1f), direction.div(length)))
        }

        renderCuboid(consumer, poseStack.last(), length, thickness, vOffset)

        poseStack.popPose()
    }

    @Suppress("DuplicatedCode", "UnnecessaryVariable")
    fun renderCuboid(
        consumer: VertexConsumer,
        pose: PoseStack.Pose,
        length: Float,
        thickness: Float,
        vOffset: Float
    ) {
        val minZ = 0f
        val maxZ = -length
        val minX = -thickness
        val maxX = thickness
        val minY = -thickness
        val maxY = thickness
        val minU = 0f
        val maxU = 1f
        val minV = vOffset
        val maxV = vOffset + length

        renderVertex(consumer, pose, minX, minY, minZ, maxU, minV, Vector3f(1f, 0f, 0f))
        renderVertex(consumer, pose, minX, minY, maxZ, maxU, maxV, Vector3f(1f, 0f, 0f))
        renderVertex(consumer, pose, minX, maxY, maxZ, minU, maxV, Vector3f(1f, 0f, 0f))
        renderVertex(consumer, pose, minX, maxY, minZ, minU, minV, Vector3f(1f, 0f, 0f))

        renderVertex(consumer, pose, maxX, minY, minZ, minU, minV, Vector3f(1f, 0f, 0f))
        renderVertex(consumer, pose, maxX, minY, maxZ, minU, maxV, Vector3f(1f, 0f, 0f))
        renderVertex(consumer, pose, maxX, maxY, maxZ, maxU, maxV, Vector3f(1f, 0f, 0f))
        renderVertex(consumer, pose, maxX, maxY, minZ, maxU, minV, Vector3f(1f, 0f, 0f))

        renderVertex(consumer, pose, minX, minY, minZ, minU, minV, Vector3f(0f, -1f, 0f))
        renderVertex(consumer, pose, maxX, minY, minZ, maxU, minV, Vector3f(0f, -1f, 0f))
        renderVertex(consumer, pose, maxX, minY, maxZ, maxU, maxV, Vector3f(0f, -1f, 0f))
        renderVertex(consumer, pose, minX, minY, maxZ, minU, maxV, Vector3f(0f, -1f, 0f))

        renderVertex(consumer, pose, minX, maxY, minZ, minU, minV, Vector3f(0f, 1f, 0f))
        renderVertex(consumer, pose, maxX, maxY, minZ, maxU, minV, Vector3f(0f, 1f, 0f))
        renderVertex(consumer, pose, maxX, maxY, maxZ, maxU, maxV, Vector3f(0f, 1f, 0f))
        renderVertex(consumer, pose, minX, maxY, maxZ, minU, maxV, Vector3f(0f, 1f, 0f))
    }

    fun renderVertex(
        consumer: VertexConsumer,
        pose: PoseStack.Pose,
        x: Float,
        y: Float,
        z: Float,
        u: Float,
        v: Float,
        normal: Vector3f
    ) {
        consumer
            .addVertex(pose, x, y, z)
            .setColor(-1)
            .setUv(u, v)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(15728880)
            .setNormal(pose, normal)
    }
}
