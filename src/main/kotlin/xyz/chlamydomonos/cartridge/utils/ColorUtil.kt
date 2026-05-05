package xyz.chlamydomonos.cartridge.utils

import net.minecraft.util.ColorRGBA
import kotlin.math.abs
import kotlin.math.roundToInt

object ColorUtil {
    fun rgbaAsInt(color: Long): Int {
        val colorRGB = (color shr 8) and 0xffffff
        val colorA = color and 0xff
        val colorARGB = (colorA shl 24) or colorRGB
        return if (colorARGB > 0x7fffffff) (colorARGB - 0x100000000).toInt() else colorARGB.toInt()
    }

    fun rgbaAsInt(r: Int, g:Int, b: Int, a: Int): Int {
        return rgbaAsInt((r.toLong() shl 24) + (g.toLong() shl 16) + (b.toLong() shl 8) + a.toLong())
    }

    fun rgbaAsInt(rgb: Int, a: Int): Int {
        return rgbaAsInt((rgb.toLong() shl 8) + a.toLong())
    }

    fun rgbAsInt(r: Int, g: Int, b: Int): Int {
        return rgbaAsInt(r, g, b, 0xff)
    }

    fun rgbAsInt(rgb: Int): Int {
        return rgbaAsInt(rgb, 0xff)
    }

    fun hsvAsInt(h: Int, s: Float, v: Float): Int {
        val hue = ((h % 360) + 360) % 360
        val sat = s.coerceIn(0f, 1f)
        val value = v.coerceIn(0f, 1f)

        if (sat == 0f) {
            val gray = (value * 255f).roundToInt().coerceIn(0, 255)
            return rgbAsInt(gray, gray, gray)
        }

        val c = value * sat
        val x = c * (1f - abs((hue / 60f) % 2f - 1f))
        val m = value - c

        val (r1, g1, b1) = when (hue) {
            in 0 until 60 -> Triple(c, x, 0f)
            in 60 until 120 -> Triple(x, c, 0f)
            in 120 until 180 -> Triple(0f, c, x)
            in 180 until 240 -> Triple(0f, x, c)
            in 240 until 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val r = ((r1 + m) * 255f).roundToInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255f).roundToInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255f).roundToInt().coerceIn(0, 255)
        return rgbAsInt(r, g, b)
    }

    fun rgba(r: Int, g: Int, b: Int, a: Int): ColorRGBA {
        return ColorRGBA(rgbaAsInt(r, g, b, a))
    }

    fun rgba(rgba: Long): ColorRGBA {
        return ColorRGBA(rgbaAsInt(rgba))
    }

    fun rgba(rgb: Int, a: Int): ColorRGBA {
        return ColorRGBA(rgbaAsInt(rgb, a))
    }

    fun rgb(r: Int, g: Int, b: Int): ColorRGBA {
        return rgba(r, g, b, 0xff)
    }

    fun rgb(rgb: Int): ColorRGBA {
        return rgba(rgb, 0xff)
    }

    fun hsv(h: Int, s: Float, v: Float): ColorRGBA {
        return ColorRGBA(hsvAsInt(h, s, v))
    }
}