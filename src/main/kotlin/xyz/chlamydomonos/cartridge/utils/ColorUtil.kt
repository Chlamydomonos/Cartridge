package xyz.chlamydomonos.cartridge.utils

object ColorUtil {
    fun rgba(color: Long): Int {
        val colorRGB = (color shr 8) and 0xffffff
        val colorA = color and 0xff
        val colorARGB = (colorA shl 24) or colorRGB
        return if (colorARGB > 0x7fffffff) (colorARGB - 0x100000000).toInt() else colorARGB.toInt()
    }

    fun rgba(rgb: Int, a: Int): Int {
        return rgba((rgb.toLong() shl 8) + a.toLong())
    }

    fun rgb(rgb: Int): Int {
        return rgba(rgb, 0xff)
    }
}