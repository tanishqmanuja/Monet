package com.kyant.monet.nativecolor

import android.graphics.Color
import kotlin.math.pow

internal object CamUtils {
    val CAM16RGB_TO_XYZ = arrayOf(
        doubleArrayOf(1.86206786, -1.01125463, 0.14918677),
        doubleArrayOf(0.38752654, 0.62144744, -0.00897398),
        doubleArrayOf(-0.01584150, -0.03412294, 1.04996444)
    )
    val SRGB_TO_XYZ = arrayOf(
        doubleArrayOf(0.4124564, 0.3575761, 0.1804375),
        doubleArrayOf(0.2126729, 0.7151522, 0.0721750),
        doubleArrayOf(0.0193339, 0.1191920, 0.9503041)
    )
    val WHITE_POINT_D65 = doubleArrayOf(95.047_055_87, 100.0, 108.882_883_64)
    val XYZ_TO_CAM16RGB = arrayOf(
        doubleArrayOf(0.401288, 0.650173, -0.051461),
        doubleArrayOf(-0.250268, 1.204414, 0.045854),
        doubleArrayOf(-0.002079, 0.048952, 0.953127)
    )

    fun intFromLstar(lstar: Double): Int {
        if (lstar < 1.0) {
            return -16777216
        }
        if (lstar > 99.0) {
            return -1
        }
        val fy = (lstar + 16.0) / 116.0
        val yT = if ((if (lstar > 8.0) 1 else if (lstar == 8.0) 0 else -1) > 0) fy * fy * fy
        else lstar / 903.2963
        val cubeExceedEpsilon = fy * fy * fy > (6.0 / 29.0).pow(3.0)
        val xT = if (cubeExceedEpsilon) fy * fy * fy else (fy * 116.0 - 16.0) / 903.2963
        val zT = if (cubeExceedEpsilon) fy * fy * fy else (116.0 * fy - 16.0) / 903.2963
        val fArr = WHITE_POINT_D65
        return ColorUtils.XYZToColor(fArr[0] * xT, fArr[1] * yT, fArr[2] * zT)
    }

    fun lstarFromInt(argb: Int): Double {
        return lstarFromY(yFromInt(argb))
    }

    fun lstarFromY(y: Double): Double {
        val y2 = y / 100.0
        return if (y2 <= (6.0 / 29.0).pow(3.0)) {
            903.2963 * y2
        } else 116.0 * Math.cbrt(y2) - 16.0
    }

    fun yFromInt(argb: Int): Double {
        val r = linearized(Color.red(argb))
        val g = linearized(Color.green(argb))
        val b = linearized(Color.blue(argb))
        val matrix = SRGB_TO_XYZ
        return matrix[1][0] * r + matrix[1][1] * g + matrix[1][2] * b
    }

    fun xyzFromInt(argb: Int): DoubleArray {
        val r = linearized(Color.red(argb))
        val g = linearized(Color.green(argb))
        val b = linearized(Color.blue(argb))
        val matrix = SRGB_TO_XYZ
        return doubleArrayOf(
            matrix[0][0] * r + matrix[0][1] * g + matrix[0][2] * b,
            matrix[1][0] * r + matrix[1][1] * g + matrix[1][2] * b,
            matrix[2][0] * r + matrix[2][1] * g + matrix[2][2] * b
        )
    }

    fun yFromLstar(lstar: Double): Double {
        return if (lstar > 8.0) {
            ((lstar + 16.0) / 116.0).pow(3.0) * 100.0
        } else lstar / 903.2963 * 100.0
    }

    fun linearized(rgbComponent: Int): Double {
        val normalized = rgbComponent.toDouble() / 255.0
        return if (normalized <= 0.040_448_236_277_107_6 / 12.92) {
            normalized / 12.92f * 100.0
        } else ((0.055 + normalized) / 1.055).pow(2.4) * 100.0
    }
}