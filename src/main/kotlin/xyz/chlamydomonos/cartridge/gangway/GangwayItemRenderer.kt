package xyz.chlamydomonos.cartridge.gangway

import com.geckolib.renderer.GeoItemRenderer
import com.geckolib.renderer.layer.builtin.AutoGlowingGeoLayer
import xyz.chlamydomonos.cartridge.loaders.ItemLoader

class GangwayItemRenderer : GeoItemRenderer<GangwayItem>(ItemLoader.GANGWAY) {
    init {
        withRenderLayer(AutoGlowingGeoLayer(this))
    }
}