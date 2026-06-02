package xyz.chlamydomonos.cartridge.sparagmos

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.texture.OverlayTexture
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Vector3f
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber(value = [Dist.CLIENT])
object SparagmosLaserRenderer {
    fun createRenderType(texture: String) = RenderTypes.entityTranslucentEmissive(
        RLUtil.of("textures/misc/${texture}.png")
    )

    const val INNER_LAYER_THICKNESS = 0.2f
    const val OUTER_LAYER_THICKNESS = 0.3f
    const val INNER_V_RATE = 0.001f
    const val OUTER_V_RATE = -0.4f

    val innerLayer = createRenderType("beam_inner")
    val outerLayer = createRenderType("beam_outer")

    data class Beam(
        val pos: Vector3f,
        val pitch: Float,
        val yaw: Float,
        var frames: Int,
        val lifetime: Int,
    )

    val beams = mutableSetOf<Beam>()

    @SubscribeEvent
    fun onClientTick(@Suppress("unused") event: ClientTickEvent.Pre) {
        val iterator = beams.iterator()
        while (iterator.hasNext()) {
            val value = iterator.next()
            value.frames++
            if (value.frames > value.lifetime) {
                iterator.remove()
            }
        }
    }

    @SubscribeEvent
    fun onRenderLevel(event: RenderLevelStageEvent.AfterTranslucentFeatures) {
        val poseStack = event.poseStack
        val cameraPos = event.levelRenderState.cameraRenderState.pos

        val innerConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(innerLayer)
        for (beam in beams) {
            renderBeam(
                innerConsumer,
                poseStack,
                beam.pos.x - cameraPos.x.toFloat(),
                beam.pos.y - cameraPos.y.toFloat(),
                beam.pos.z - cameraPos.z.toFloat(),
                beam.pitch,
                beam.yaw,
                SparagmosHandler.LENGTH,
                INNER_LAYER_THICKNESS,
                beam.frames * INNER_V_RATE
            )
        }

        val outerConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(outerLayer)
        for (beam in beams) {
            renderBeam(
                outerConsumer,
                poseStack,
                beam.pos.x - cameraPos.x.toFloat(),
                beam.pos.y - cameraPos.y.toFloat(),
                beam.pos.z - cameraPos.z.toFloat(),
                beam.pitch,
                beam.yaw,
                SparagmosHandler.LENGTH,
                OUTER_LAYER_THICKNESS,
                beam.frames * OUTER_V_RATE
            )
        }
    }

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