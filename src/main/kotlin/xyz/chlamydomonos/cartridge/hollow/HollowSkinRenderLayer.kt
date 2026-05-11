package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.constant.dataticket.DataTicket
import com.geckolib.renderer.base.GeoRenderState
import com.geckolib.renderer.base.RenderPassInfo
import com.geckolib.renderer.layer.GeoRenderLayer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.rendertype.RenderTypes
import xyz.chlamydomonos.cartridge.utils.RLUtil
import xyz.chlamydomonos.cartridge.utils.forData

class HollowSkinRenderLayer<R>(
    renderer: HollowRenderer<R>
) : GeoRenderLayer<HollowEntity, Void, R>(renderer) where R : EntityRenderState, R : GeoRenderState {
    companion object {
        val COLOR = DataTicket.create("color", Int::class.java)
        val RenderPassInfo<*>.color get() = forData(COLOR)

        val TEXTURE_PATH = RLUtil.of("textures/entity/hollow/skin.png")
    }

    override fun addRenderData(animatable: HollowEntity, relatedObject: Void?, renderState: R, partialTick: Float) {
        renderState.addGeckolibData(COLOR, animatable.color)
    }

    override fun submitRenderTask(renderPassInfo: RenderPassInfo<R>, renderTasks: SubmitNodeCollector) {
        val bakedModel = renderPassInfo.model()

        renderTasks.submitCustomGeometry(
            renderPassInfo.poseStack(),
            RenderTypes.entityCutout(TEXTURE_PATH)
        ) { pose, vertexConsumer ->
            val poseStack = renderPassInfo.poseStack()
            poseStack.pushPose()
            poseStack.last().set(pose)
            renderPassInfo.renderPosed {
                bakedModel.render(
                    renderPassInfo,
                    vertexConsumer,
                    renderPassInfo.packedLight(),
                    renderPassInfo.packedOverlay(),
                    renderPassInfo.color
                )
            }
            poseStack.popPose()
        }
    }
}