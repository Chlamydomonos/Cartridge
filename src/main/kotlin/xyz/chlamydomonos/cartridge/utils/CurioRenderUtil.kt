package xyz.chlamydomonos.cartridge.utils

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.geom.ModelPart
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
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer

object CurioRenderUtil {
    fun <S : LivingEntityRenderState, M : EntityModel<in S>> renderItemStack(
        renderLayerParent: RenderLayerParent<S, M>,
        renderState: S,
        poseStack: PoseStack,
        context: EntityRendererProvider.Context,
        stack: ItemStack,
        slotContext: SlotContext,
        submitNodeCollector: SubmitNodeCollector,
        packedLight: Int,
        modelPart: (HumanoidModel<*>) -> ModelPart,
        poseProfile: PoseUtil.Profile
    ) {
        val parentModel = renderLayerParent.model
        if (parentModel !is HumanoidModel<*> || renderState !is HumanoidRenderState) {
            return
        }

        ICurioRenderer.setupHumanoidAnimations(parentModel, renderState)

        val itemState = ItemStackRenderState()

        context.itemModelResolver.updateForNonLiving(
            itemState,
            stack,
            ItemDisplayContext.NONE,
            slotContext.entity
        )

        poseStack.pushPose()

        PoseUtil.applyModelPartTransform(poseStack, modelPart(parentModel), poseProfile)

        itemState.submit(
            poseStack,
            submitNodeCollector,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            EntityRenderState.NO_OUTLINE
        )

        poseStack.popPose()
    }
}