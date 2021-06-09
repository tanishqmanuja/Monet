package com.kyant.monet.color

import androidx.compose.ui.graphics.Color
import com.kyant.monet.colorscience.CAM16
import com.kyant.monet.colorscience.CAM16Parameters
import smile.clustering.kmeans

fun List<Color>.findCentroids(
    k: Int,
    param: CAM16Parameters = DefaultMonetParameters
): List<Color> = kmeans(map {
    doubleArrayOf(it.red.toDouble(), it.green.toDouble(), it.blue.toDouble())
}.toTypedArray(), k, 15, 0.01, 10).centroids.map {
    Color(
        (0xFF * it[0]).toInt().coerceIn(0..255),
        (0xFF * it[1]).toInt().coerceIn(0..255),
        (0xFF * it[2]).toInt().coerceIn(0..255)
    )
}.sortedBy { CAM16.fromColor(it, param).s }