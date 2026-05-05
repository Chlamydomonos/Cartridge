package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.constant.DefaultAnimations
import com.geckolib.renderer.GeoEntityRenderer
import com.geckolib.renderer.base.BoneSnapshots
import com.geckolib.renderer.base.GeoRenderState
import com.geckolib.renderer.base.RenderPassInfo
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.world.entity.EntityType

class HollowRenderer<R>(
    context: EntityRendererProvider.Context,
    entityType: EntityType<out HollowEntity>
) : GeoEntityRenderer<HollowEntity, R>(context, entityType) where R : EntityRenderState, R : GeoRenderState {
    override fun adjustModelBonesForRender(renderPassInfo: RenderPassInfo<R>, snapshots: BoneSnapshots) {
        DefaultAnimations.hardcodedHeadRotation(renderPassInfo, snapshots, "head")
    }
}