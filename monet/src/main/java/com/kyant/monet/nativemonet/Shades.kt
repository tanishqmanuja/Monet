package com.kyant.monet.nativemonet

import com.kyant.monet.nativecolor.ColorUtils

internal object Shades {
    fun of(hue: Double, chroma: Double): List<Int> {
        val iArr = IntArray(12)
        iArr[0] = ColorUtils.CAMToColor(hue, chroma, 99.0)
        iArr[1] = ColorUtils.CAMToColor(hue, chroma, 95.0)
        var i = 2
        while (i < 12) {
            iArr[i] =
                ColorUtils.CAMToColor(hue, chroma, if (i == 6) 49.6 else 100.0 - (i - 1.0) * 10.0)
            i++
        }
        return iArr.toList()
    }
}