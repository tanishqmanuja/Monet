package com.kyant.monet.color

import androidx.compose.ui.graphics.Color
import com.kyant.monet.colorscience.CAM16
import com.kyant.monet.colorscience.CAM16.Companion.gamutMap
import com.kyant.monet.colorscience.CAM16Parameters
import com.kyant.monet.colorscience.Contrast.toY
import com.kyant.monet.colorscience.RGB
import com.kyant.monet.colorscience.SRGB.toXYZ
import com.kyant.monet.colorscience.SRGB.tosRGB
import com.kyant.monet.colorscience.XYZ
import com.kyant.monet.math.Matrix3x1
import com.kyant.monet.math.matrix3x1Of

class MonetColor(rgb: RGB, param: CAM16Parameters = CAM16Parameters.Default) {
    private val colorScheme = colorSchemeOf(rgb.toXYZ(param), param)
    val accent1 = colorScheme[0]
    val accent2 = colorScheme[1]
    val accent3 = colorScheme[2]
    val neutral1 = colorScheme[3]
    val neutral2 = colorScheme[4]

    override fun toString(): String = """ColorScheme(
  accent1: ${accent1.map { it.toColor() }}
  accent2: ${accent2.map { it.toColor() }}
  accent3: ${accent3.map { it.toColor() }}
  neutral1: ${neutral1.map { it.toColor() }}
  neutral2: ${neutral2.map { it.toColor() }}
)"""

    operator fun component1(): List<Matrix3x1> = accent1
    operator fun component2(): List<Matrix3x1> = accent2
    operator fun component3(): List<Matrix3x1> = accent3
    operator fun component4(): List<Matrix3x1> = neutral1
    operator fun component5(): List<Matrix3x1> = neutral2

    companion object {
        fun Color.toRGB(): RGB = matrix3x1Of(red.toDouble(), green.toDouble(), blue.toDouble())

        fun RGB.toColor(): Color = Color(
            (0xFF * this[0]).toInt().coerceIn(0..255),
            (0xFF * this[1]).toInt().coerceIn(0..255),
            (0xFF * this[2]).toInt().coerceIn(0..255)
        )

        private fun shadesOf(
            h: Double,
            C: Double,
            param: CAM16Parameters = CAM16Parameters.Default
        ): Array<Matrix3x1> = arrayOf(
            gamutMap(95.0.toY(), C, h, param),
            gamutMap(90.0.toY(), C, h, param),
            gamutMap(80.0.toY(), C, h, param),
            gamutMap(70.0.toY(), C, h, param),
            gamutMap(60.0.toY(), C, h, param),
            gamutMap(49.0.toY(), C, h, param),
            gamutMap(40.0.toY(), C, h, param),
            gamutMap(30.0.toY(), C, h, param),
            gamutMap(20.0.toY(), C, h, param),
            gamutMap(10.0.toY(), C, h, param),
            gamutMap(0.0.toY(), C, h, param)
        )

        private fun colorSchemeOf(
            xyz: XYZ,
            param: CAM16Parameters = CAM16Parameters.Default
        ): List<List<RGB>> {
            val cam16color = CAM16(xyz, param)
            val y = xyz[1]

            val color = CAM16(gamutMap(y, 48.0, cam16color.h, param), param)
            val shades = shadesOf(color.h, color.c, param)

            val color2 = CAM16(gamutMap(y, 16.0, cam16color.h, param), param)
            val shades2 = shadesOf(color2.h, color2.c, param)

            val color3 = CAM16(gamutMap(y, 32.0, cam16color.h + 60.0, param), param)
            val shades3 = shadesOf(color3.h, color3.c, param)

            val color4 = CAM16(gamutMap(y, 8.0, cam16color.h, param), param)
            val shades4 = shadesOf(color4.h, color4.c, param)

            val color5 = CAM16(gamutMap(y, 4.0, cam16color.h, param), param)
            val shades5 = shadesOf(color5.h, color5.c, param)

            return arrayOf(shades, shades2, shades3, shades4, shades5)
                .map { shade -> shade.map { it.tosRGB(param) } }
        }
    }
}