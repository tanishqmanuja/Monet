package com.kyant.monet

import com.kyant.monet.nativecolor.Cam

class ColorScheme(rgb: Int, private val darkTheme: Boolean = false) {
    val hue: Double = Cam.fromInt(rgb).hue
    val accent1 = Shades.of(hue, 48.0)
    val accent2 = Shades.of(hue, 16.0)
    val accent3 = Shades.of(hue + 60.0, 32.0)
    val neutral1 = Shades.of(hue, 4.0)
    val neutral2 = Shades.of(hue, 8.0)
}