package com.kyant.monet.nativecolor

import android.graphics.Color
import kotlin.math.pow
import kotlin.math.roundToInt

object ColorUtils {
    private const val XYZ_WHITE_REFERENCE_Y = 100.0

    fun colorToCAM(color: Int): Cam {
        return Cam.fromInt(color)
    }

    fun CAMToColor(hue: Double, chroma: Double, lstar: Double): Int {
        return Cam.getInt(hue, chroma, lstar)
    }

    fun XYZToColor(x: Double, y: Double, z: Double): Int {
        val r = (3.2404542 * x + -1.5371385 * y + -0.4985314 * z) / XYZ_WHITE_REFERENCE_Y
        val g = (-0.9692660 * x + 1.8760108 * y + 0.0415560 * z) / XYZ_WHITE_REFERENCE_Y
        val b = (0.0556434 * x + -0.2040259 * y + 1.0572252 * z) / XYZ_WHITE_REFERENCE_Y
        return Color.rgb(
            ((if (r > 0.040_448_236_277_107_6 / 12.92) r.pow(1.0 / 2.4) * 1.055 - 0.055 else r * 12.92) * 255.0).roundToInt()
                .coerceIn(0..255),
            ((if (g > 0.040_448_236_277_107_6 / 12.92) g.pow(1.0 / 2.4) * 1.055 - 0.055 else g * 12.92) * 255.0).roundToInt()
                .coerceIn(0..255),
            ((if (b > 0.040_448_236_277_107_6 / 12.92) b.pow(1.0 / 2.4) * 1.055 - 0.055 else b * 12.92) * 255.0).roundToInt()
                .coerceIn(0..255),
        )
    }
}