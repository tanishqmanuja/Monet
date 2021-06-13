package com.kyant.monet

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.kyant.monet.nativecolor.ColorUtils
import smile.clustering.kmeans
import android.graphics.Color as NativeColor

fun List<Color>.colorCentroidsOf(k: Int): List<Color> = this.map {
    it.toArgb()
}.toList().rgbCentroids(k).map {
    Color(it.red, it.green, it.blue)
}

fun List<Int>.rgbCentroids(k: Int): List<Int> = kmeans(map {
    doubleArrayOf(it.red.toDouble(), it.green.toDouble(), it.blue.toDouble())
}.toTypedArray(), k, 15, 0.01, 10).centroids.map {
    NativeColor.rgb(it[0].toInt(), it[1].toInt(), it[2].toInt())
}.sortedByDescending { ColorUtils.colorToCAM(it).s }.map { it }