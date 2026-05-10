package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.cache.model.GeoBone
import com.geckolib.cache.model.cuboid.CuboidGeoBone
import com.geckolib.constant.dataticket.DataTicket
import com.geckolib.renderer.base.GeoRenderState
import com.geckolib.renderer.base.GeoRenderer
import com.geckolib.renderer.base.RenderPassInfo
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer
import com.geckolib.util.RenderUtil
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.resources.Identifier
import xyz.chlamydomonos.cartridge.hollow.HollowSkinRenderLayer.Companion.color
import xyz.chlamydomonos.cartridge.utils.RLUtil

class HollowFaceRenderLayer<R, D : Any>(
    renderer: GeoRenderer<HollowEntity, Void, R>,
    boneName: String,
    val data: DataTicket<D>,
    val hasColor: (D) -> Boolean,
    val textureSelector: (D) -> Identifier
) : CustomBoneTextureGeoLayer<HollowEntity, Void, R>(
    renderer,
    boneName,
    INITIAL_TEXTURE
) where R : EntityRenderState, R : GeoRenderState {
    companion object {
        val INITIAL_TEXTURE = RLUtil.of("this_should_not_appear")
    }

    override fun getTextureResource(renderState: R) = textureSelector(
        renderState.getGeckolibData(data) ?: throw RuntimeException("Missing data ticket")
    )

    @Suppress("UnstableApiUsage")
    override fun renderBone(renderPassInfo: RenderPassInfo<R>, bone: GeoBone, renderTasks: SubmitNodeCollector) {
        val renderState = renderPassInfo.renderState()
        val boneTexture = getTextureResource(renderState)
        val baseTexture = this.renderer.getTextureLocation(renderState)
        val boneTextureSize = RenderUtil.getTextureDimensions(boneTexture)
        val baseTextureSize = RenderUtil.getTextureDimensions(baseTexture)
        val widthRatio = baseTextureSize.firstInt() / boneTextureSize.firstInt().toFloat()
        val heightRatio = baseTextureSize.secondInt() / boneTextureSize.secondInt().toFloat()
        val packedLight = renderPassInfo.packedLight()
        val packedOverlay = renderPassInfo.packedOverlay()
        val renderColor = if (hasColor(renderPassInfo.getGeckolibData(data)!!)) {
            renderPassInfo.color
        } else {
            renderPassInfo.renderColor()
        }
        val renderType = getRenderType(renderState, boneTexture)

        if (renderType != null) {
            renderTasks.submitCustomGeometry(
                renderPassInfo.poseStack(),
                renderType
            ) { pose, buffer ->
                val poseStack = renderPassInfo.poseStack()
                poseStack.pushPose()
                poseStack.last().set(pose)
                bone.translateAwayFromPivotPoint(poseStack)

                for (cube in (bone as CuboidGeoBone).cubes) {
                    poseStack.pushPose()
                    renderCube(
                        cube,
                        poseStack,
                        buffer,
                        packedLight,
                        packedOverlay,
                        renderColor,
                        widthRatio,
                        heightRatio
                    )
                    poseStack.popPose()
                }
                poseStack.popPose()
            }
        }
    }
}