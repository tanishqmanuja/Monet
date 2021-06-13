package com.kyant.monet

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.kyant.monet.nativecolor.Cam
import com.kyant.monet.nativecolor.ColorUtils
import smile.clustering.kmeans
import android.graphics.Color as NativeColor

fun List<Color>.rgbCentroids(k: Int): List<Color> = kmeans(map {
    with(it.toArgb()) {
        doubleArrayOf(red.toDouble(), green.toDouble(), blue.toDouble())
    }
}.toTypedArray(), k, 15, 0.01, 10).centroids
    .map { NativeColor.rgb(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
    .sortedByDescending { ColorUtils.colorToCAM(it).s }
    .map { Color(it.red, it.green, it.blue) }

fun List<Color>.cam16Centroids(k: Int): List<Color> = kmeans(map {
    with(ColorUtils.colorToCAM(it.toArgb())) {
        doubleArrayOf(hue, chroma, j, q, m, s, jstar, astar, bstar)
    }
}.toTypedArray(), k, 15, 0.01, 10).centroids
    .map { Cam(it[0], it[1], it[2], it[3], it[4], it[5], it[6], it[7], it[8]).viewedInSrgb() }
    .sortedByDescending { ColorUtils.colorToCAM(it).s }
    .map { Color(it.red, it.green, it.blue) }