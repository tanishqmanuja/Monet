package com.kyant.monet.nativemonet

import com.kyant.monet.nativecolor.Cam

class ColorScheme(rgb: Int, private val darkTheme: Boolean = false) {
    val accent1: List<Int>
    val accent2: List<Int>
    val accent3: List<Int>
    val neutral1: List<Int>
    val neutral2: List<Int>

    private fun humanReadable(list: List<Int>): String {
        return list.joinToString { "#${it.toString(16)}" }
    }

    override fun toString(): String {
        return """ColorScheme(
neutral1: ${humanReadable(neutral1)}, 
neutral2: ${humanReadable(neutral2)}, 
accent1: ${humanReadable(accent1)}, 
accent2: ${humanReadable(accent2)}, 
accent3: ${humanReadable(accent3)})"""
    }

    init {
        val hue: Float = Cam.fromInt(rgb).hue
        accent1 = Shades.of(hue, 48.0f)
        accent2 = Shades.of(hue, 16.0f)
        accent3 = Shades.of(60.0f + hue, 32.0f)
        neutral1 = Shades.of(hue, 4.0f)
        neutral2 = Shades.of(hue, 8.0f)
    }
}