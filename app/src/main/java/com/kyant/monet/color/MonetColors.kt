package com.kyant.monet.color

import androidx.compose.ui.graphics.Color
import com.kyant.monet.color.MonetColor.Companion.toColor
import com.kyant.monet.color.MonetColor.Companion.toRGB

data class MonetColors(
    val accent1: List<Color> = emptyList(),
    val accent2: List<Color> = emptyList(),
    val accent3: List<Color> = emptyList(),
    val neutral1: List<Color> = emptyList(),
    val neutral2: List<Color> = emptyList()
)

fun monetColors(color: Color): MonetColors {
    val (a1, a2, a3, n1, n2) = MonetColor(color.toRGB())
    return MonetColors(
        a1.map { it.toColor() },
        a2.map { it.toColor() },
        a3.map { it.toColor() },
        n1.map { it.toColor() },
        n2.map { it.toColor() }
    )
}