package xyz.chlamydomonos.cartridge.cartridge

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer
import xyz.chlamydomonos.cartridge.utils.CurioRenderUtil
import xyz.chlamydomonos.cartridge.utils.PoseUtil

class BackpackRenderer : ICurioRenderer {
    companion object {
        val poseProfile = PoseUtil.Profile(
            Vec3(0.0, -2.0 - 1.0 / 32, -5.0 + 1.0/32),
            Vec3(-4.0, 0.0, -2.0),
            Vec3(8.0, 12.0, 4.0)
        )
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
        CurioRenderUtil.renderItemStack(
            renderLayerParent,
            renderState,
            poseStack,
            context,
            stack,
            slotContext,
            submitNodeCollector,
            packedLight,
            { it.body },
            poseProfile
        )
    }
}