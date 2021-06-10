package com.kyant.monet.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.kyant.monet.nativecolor.Cam
import com.kyant.monet.nativecolor.ColorUtils

class MonetColor(i: Int) {
    private val colorScheme = colorSchemeOf(i)
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

    operator fun component1(): List<Int> = accent1
    operator fun component2(): List<Int> = accent2
    operator fun component3(): List<Int> = accent3
    operator fun component4(): List<Int> = neutral1
    operator fun component5(): List<Int> = neutral2

    companion object {
        fun Color.toRGB(): Int = this.toArgb()

        fun Int.toColor(): Color = Color(this)

        private fun shadesOf(h: Float, C: Float): List<Int> = listOf(
            ColorUtils.CAMToColor(h, C, 99.0f),
            ColorUtils.CAMToColor(h, C, 95.0f),
            ColorUtils.CAMToColor(h, C, 90.0f),
            ColorUtils.CAMToColor(h, C, 80.0f),
            ColorUtils.CAMToColor(h, C, 70.0f),
            ColorUtils.CAMToColor(h, C, 60.0f),
            ColorUtils.CAMToColor(h, C, 49.0f),
            ColorUtils.CAMToColor(h, C, 40.0f),
            ColorUtils.CAMToColor(h, C, 30.0f),
            ColorUtils.CAMToColor(h, C, 20.0f),
            ColorUtils.CAMToColor(h, C, 10.0f),
            ColorUtils.CAMToColor(h, C, 0.0f)
        )

        private fun colorSchemeOf(i: Int): List<List<Int>> {
            val hue = Cam.fromInt(i).hue

            val accent1 = shadesOf(hue, 48.0f)
            val accent2 = shadesOf(hue, 16.0f)
            val accent3 = shadesOf(32.0f + hue, 48.0f)
            val neutral1 = shadesOf(hue, 4.0f)
            val neutral2 = shadesOf(hue, 8.0f)

            return listOf(accent1, accent2, accent3, neutral1, neutral2)
        }
    }
}