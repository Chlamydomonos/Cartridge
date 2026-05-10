package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.constant.dataticket.DataTicket
import com.geckolib.renderer.base.GeoRenderState
import com.geckolib.renderer.base.GeoRenderer
import com.geckolib.renderer.layer.builtin.CustomBoneTextureGeoLayer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import xyz.chlamydomonos.cartridge.utils.RLUtil

class HollowEarRenderLayer<R>(
    renderer: GeoRenderer<HollowEntity, Void, R>,
    boneName: String,
    val dataTicket: DataTicket<HollowEntityData.EarType>,
    val isUp: Boolean
) : CustomBoneTextureGeoLayer<HollowEntity, Void, R>(
    renderer,
    boneName,
    RLUtil.of("textures/entity/hollow/ear.png")
) where R : EntityRenderState, R : GeoRenderState {
    override fun shouldRenderBone(renderState: R) = if (isUp) {
        renderState.getGeckolibData(dataTicket) == HollowEntityData.EarType.UP
    } else {
        renderState.getGeckolibData(dataTicket) == HollowEntityData.EarType.DOWN
    }
}