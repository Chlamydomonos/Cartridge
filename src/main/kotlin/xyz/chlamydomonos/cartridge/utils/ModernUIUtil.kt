package xyz.chlamydomonos.cartridge.utils

object ModernUIUtil {
    val isModernUILoaded by lazy {
        try {
            Class.forName("icyllis.modernui.mc.neoforge.ModernUIForge")
            true
        } catch (_: Exception) {
            false
        }
    }

    fun artText(key: String): String {
        return if (isModernUILoaded) {
            key
        } else {
            "${key}.fallback"
        }
    }
}