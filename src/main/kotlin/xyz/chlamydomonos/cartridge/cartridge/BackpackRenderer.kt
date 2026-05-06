package xyz.chlamydomonos.cartridge.cartridge

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.HumanoidRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.item.ItemStackRenderState
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer
import kotlin.math.PI

class BackpackRenderer : ICurioRenderer {
    override fun <S : LivingEntityRenderState, M : EntityModel<in S>> render(
        stack: ItemStack,
        slotContext: SlotContext,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        packedLight: Int,
        renderState: S,
        renderLayerParent: RenderLayerParent<S, M>,
        context: EntityRendererProvider.Context,
        yRotation: Float,
        xRotation: Float
    ) {
        val parentModel = renderLayerParent.model
        if (parentModel !is HumanoidModel<*> || renderState !is HumanoidRenderState) {
            return
        }

        ICurioRenderer.setupHumanoidAnimations(parentModel, renderState)

        val renderState = ItemStackRenderState()

        context.itemModelResolver.updateForNonLiving(
            renderState,
            stack,
            ItemDisplayContext.NONE,
            slotContext.entity
        )

        poseStack.pushPose()

        poseStack.rotateAround(
            Quaternionf(AxisAngle4f(PI.toFloat(), 0f, 0f, 1f)),
            0f,
            0f,
            0f
        )

        poseStack.translate(0f, -0.249f, 0.3f)

        poseStack.rotateAround(
            Quaternionf(AxisAngle4f(-parentModel.body.xRot, 1f, 0f, 0f)),
            0f,
            0.15f,
            0.05f
        )

        poseStack.rotateAround(
            Quaternionf(AxisAngle4f(-parentModel.body.yRot, 0f, 1f, 0f)),
            0f,
            0f,
            -0.3f
        )

        poseStack.rotateAround(
            Quaternionf(AxisAngle4f(-parentModel.body.zRot, 0f, 0f, 1f)),
            0f,
            0.15f,
            0.05f
        )

        renderState.submit(
            poseStack,
            submitNodeCollector,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            EntityRenderState.NO_OUTLINE
        )

        poseStack.popPose()
    }
}