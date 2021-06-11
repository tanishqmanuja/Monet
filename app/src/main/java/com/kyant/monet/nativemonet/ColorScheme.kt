package com.kyant.monet.nativemonet

import com.kyant.monet.nativecolor.Cam

class ColorScheme(rgb: Int, private val darkTheme: Boolean = false) {
    val hue: Float = Cam.fromInt(rgb).hue
    val accent1 = Shades.of(hue, 48.0f)
    val accent2 = Shades.of(hue, 16.0f)
    val accent3 = Shades.of(60.0f + hue, 32.0f)
    val neutral1 = Shades.of(hue, 4.0f)
    val neutral2 = Shades.of(hue, 8.0f)
}