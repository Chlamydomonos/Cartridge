package xyz.chlamydomonos.cartridge.hollow

import com.geckolib.model.DefaultedEntityGeoModel
import com.geckolib.renderer.base.GeoRenderState
import net.minecraft.world.entity.EntityType
import xyz.chlamydomonos.cartridge.utils.RLUtil

class HollowModel(entityType: EntityType<out HollowEntity>) : DefaultedEntityGeoModel<HollowEntity>(entityType) {
    companion object {
        val TEXTURE_PATH = RLUtil.of("textures/entity/hollow/base.png")
    }

    override fun getTextureResource(renderState: GeoRenderState) = TEXTURE_PATH
}