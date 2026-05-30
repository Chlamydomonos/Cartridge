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
import net.minecraft.world.phys.Vec3
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer
import xyz.chlamydomonos.cartridge.utils.PoseUtil

class BackpackRenderer : ICurioRenderer {
    companion object {
        private val backpackCenter = Vec3(0.0, -2.0 - 1.0 / 32, -5.0 + 1.0 / 32).scale(PoseUtil.PX_SIZE)
        private val playerBodyCenter = Vec3(0.0, 6.0, 0.0).scale(PoseUtil.PX_SIZE)
    }

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

        val itemState = ItemStackRenderState()

        context.itemModelResolver.updateForNonLiving(
            itemState,
            stack,
            ItemDisplayContext.NONE,
            slotContext.entity
        )

        poseStack.pushPose()

        PoseUtil.applyModelPartTransform(
            backpackCenter,
            playerBodyCenter,
            poseStack,
            parentModel.body
        )

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