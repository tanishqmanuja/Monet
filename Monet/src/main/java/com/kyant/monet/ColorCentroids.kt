package com.kyant.monet

import android.graphics.Color
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.kyant.monet.nativecolor.ColorUtils
import smile.clustering.kmeans

fun List<Int>.rgbCentroids(k: Int): List<Int> = kmeans(map {
    doubleArrayOf(it.red.toDouble(), it.green.toDouble(), it.blue.toDouble())
}.toTypedArray(), k, 15, 0.01, 10).centroids.map {
    Color.rgb(it[0].toInt(), it[1].toInt(), it[2].toInt())
}.sortedByDescending { ColorUtils.colorToCAM(it).s }.map { it }