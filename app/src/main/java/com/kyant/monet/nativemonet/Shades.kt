package com.kyant.monet.nativemonet

import com.kyant.monet.nativecolor.ColorUtils

object Shades {
    fun of(hue: Float, chroma: Float): List<Int> {
        val iArr = IntArray(12)
        iArr[0] = ColorUtils.CAMToColor(hue, chroma, 99.0f)
        iArr[1] = ColorUtils.CAMToColor(hue, chroma, 95.0f)
        var i = 2
        while (i < 12) {
            iArr[i] =
                ColorUtils.CAMToColor(hue, chroma, if (i == 6) 49.6f else (100 - (i - 1) * 10).toFloat())
            i++
        }
        return iArr.toList()
    }
}