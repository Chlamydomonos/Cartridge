package xyz.chlamydomonos.cartridge.gangway

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.joml.Vector3f
import org.joml.Vector3fc
import xyz.chlamydomonos.cartridge.utils.BeamRenderer
import xyz.chlamydomonos.cartridge.utils.RLUtil

@EventBusSubscriber(value = [Dist.CLIENT])
object GangwayBeamRenderer {
    val renderType = RenderTypes.entityTranslucentEmissive(
        RLUtil.of("textures/misc/gangway_beam.png")
    )

    const val THICKNESS = 0.05f
    const val V_RATE = -0.4f
    const val LIFETIME = 3

    data class Beam(
        val start: Vector3fc,
        val end: Vector3fc,
        var frames: Int,
        val lifetime: Int,
    )

    val beams = mutableSetOf<Beam>()

    @SubscribeEvent
    fun onClientTick(@Suppress("unused") event: ClientTickEvent.Pre) {
        val iterator = beams.iterator()
        while (iterator.hasNext()) {
            val beam = iterator.next()
            beam.frames++
            if (beam.frames > beam.lifetime) {
                iterator.remove()
            }
        }
    }

    @SubscribeEvent
    fun onRenderLevel(event: RenderLevelStageEvent.AfterTranslucentBlocks) {
        val poseStack = event.poseStack
        val cameraPos = event.levelRenderState.cameraRenderState.pos
        val consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(renderType)

        for (beam in beams) {
            BeamRenderer.renderBeam(
                consumer,
                poseStack,
                Vector3f(beam.start).sub(cameraPos.x.toFloat(), cameraPos.y.toFloat(), cameraPos.z.toFloat()),
                Vector3f(beam.end).sub(cameraPos.x.toFloat(), cameraPos.y.toFloat(), cameraPos.z.toFloat()),
                THICKNESS,
                beam.frames * V_RATE
            )
        }
    }
}
