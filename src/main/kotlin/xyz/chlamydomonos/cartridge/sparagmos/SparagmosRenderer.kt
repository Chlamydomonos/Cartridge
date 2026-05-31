package xyz.chlamydomonos.cartridge.sparagmos

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.EntityModel
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.RenderLayerParent
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.SlotContext
import top.theillusivec4.curios.api.client.ICurioRenderer
import xyz.chlamydomonos.cartridge.loaders.ItemLoader
import xyz.chlamydomonos.cartridge.utils.CurioRenderUtil
import xyz.chlamydomonos.cartridge.utils.PoseUtil
import kotlin.jvm.optionals.getOrNull

class SparagmosRenderer : ICurioRenderer {
    companion object {
        private val rHandProfile = PoseUtil.Profile(
            Vec3(0.0, -2.0, 0.0),
            Vec3(-3.0, -2.0, -2.0),
            Vec3(4.0, 12.0, 4.0),
            Axis.YP.rotationDegrees(180f)
        )

        private val lHandProfile = PoseUtil.Profile(
            Vec3(0.0, -2.0, 0.0),
            Vec3(-1.0, -2.0, -2.0),
            Vec3(4.0, 12.0, 4.0)
        )
    }

    fun isFirstStack(slotContext: SlotContext): Boolean {
        val index = slotContext.index
        val entity = slotContext.entity
        val inventory = CuriosApi.getCuriosInventory(entity)?.getOrNull() ?: return true
        val bracelets = inventory.getStacksHandler("bracelet").getOrNull()?.stacks ?: return true
        for (i in 0..<minOf(index, bracelets.slots - 1)) {
            if (bracelets.getStackInSlot(i).`is`(ItemLoader.SPARAGMOS)) {
                return false
            }
        }
        return true
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
        val isRightHand = isFirstStack(slotContext) xor (
                Minecraft.getInstance().options.mainHand().get() == HumanoidArm.RIGHT
        )

        CurioRenderUtil.renderItemStack(
            renderLayerParent,
            renderState,
            poseStack,
            context,
            stack,
            slotContext,
            submitNodeCollector,
            packedLight,
            if (isRightHand) { { it.rightArm } } else { { it.leftArm } },
            if (isRightHand) rHandProfile else lHandProfile
        )
    }
}