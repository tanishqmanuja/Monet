package com.kyant.monet.color

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.kyant.monet.rgbCentroids

fun List<Color>.colorCentroids(k: Int): List<Color> = this.map {
    it.toArgb()
}.toList().rgbCentroids(k).map {
    Color(it.red, it.green, it.blue)
}