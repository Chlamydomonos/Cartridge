package xyz.chlamydomonos.cartridge.sparagmos

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Vector3f
import xyz.chlamydomonos.cartridge.utils.BeamRenderer
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber(value = [Dist.CLIENT])
object SparagmosBeamRenderer {
    fun createRenderType(texture: String) = RenderTypes.entityTranslucentEmissive(
        RLUtil.of("textures/misc/${texture}.png")
    )

    const val INNER_LAYER_THICKNESS = 0.2f
    const val OUTER_LAYER_THICKNESS = 0.3f
    const val INNER_V_RATE = -0.01f
    const val OUTER_V_RATE = -0.4f

    val innerLayer = createRenderType("sparagmos_beam_inner")
    val outerLayer = createRenderType("sparagmos_beam_outer")

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
    fun onRenderLevel(event: RenderLevelStageEvent.AfterTranslucentBlocks) {
        val poseStack = event.poseStack
        val cameraPos = event.levelRenderState.cameraRenderState.pos

        val innerConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(innerLayer)
        for (beam in beams) {
            BeamRenderer.renderBeam(
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
            BeamRenderer.renderBeam(
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
}