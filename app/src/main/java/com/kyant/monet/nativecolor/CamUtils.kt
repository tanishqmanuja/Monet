package com.kyant.monet.nativecolor

import android.graphics.Color

object CamUtils {
    val CAM16RGB_TO_XYZ = arrayOf(
        floatArrayOf(1.8620678f, -1.0112547f, 0.14918678f),
        floatArrayOf(0.38752654f, 0.62144744f, -0.00897398f),
        floatArrayOf(-0.0158415f, -0.03412294f, 1.0499644f)
    )
    val SRGB_TO_XYZ = arrayOf(
        floatArrayOf(0.41233894f, 0.35762063f, 0.18051042f),
        floatArrayOf(0.2126f, 0.7152f, 0.0722f),
        floatArrayOf(0.01932141f, 0.11916382f, 0.9503448f)
    )
    val WHITE_POINT_D65 = floatArrayOf(95.047f, 100.0f, 108.883f)
    val XYZ_TO_CAM16RGB = arrayOf(
        floatArrayOf(0.401288f, 0.650173f, -0.051461f),
        floatArrayOf(-0.250268f, 1.204414f, 0.045854f),
        floatArrayOf(-0.002079f, 0.048952f, 0.953127f)
    )

    fun intFromLstar(lstar: Float): Int {
        if (lstar < 1.0f) {
            return -16777216
        }
        if (lstar > 99.0f) {
            return -1
        }
        val fy = (lstar + 16.0f) / 116.0f
        val yT =
            if ((if (lstar > 8.0f) 1 else if (lstar == 8.0f) 0 else -1) > 0) fy * fy * fy else lstar / 903.2963f
        val cubeExceedEpsilon = fy * fy * fy > 0.008856452f
        val xT = if (cubeExceedEpsilon) fy * fy * fy else (fy * 116.0f - 16.0f) / 903.2963f
        val zT = if (cubeExceedEpsilon) fy * fy * fy else (116.0f * fy - 16.0f) / 903.2963f
        val fArr = WHITE_POINT_D65
        return ColorUtils.XYZToColor(
            (fArr[0] * xT).toDouble(),
            (fArr[1] * yT).toDouble(),
            (fArr[2] * zT).toDouble()
        )
    }

    fun lstarFromInt(argb: Int): Float {
        return lstarFromY(yFromInt(argb))
    }

    fun lstarFromY(y: Float): Float {
        val y2 = y / 100.0f
        return if (y2 <= 0.008856452f) {
            903.2963f * y2
        } else 116.0f * Math.cbrt(y2.toDouble()).toFloat() - 16.0f
    }

    fun yFromInt(argb: Int): Float {
        val r = linearized(Color.red(argb))
        val g = linearized(Color.green(argb))
        val b = linearized(Color.blue(argb))
        val matrix = SRGB_TO_XYZ
        return matrix[1][0] * r + matrix[1][1] * g + matrix[1][2] * b
    }

    fun xyzFromInt(argb: Int): FloatArray {
        val r = linearized(Color.red(argb))
        val g = linearized(Color.green(argb))
        val b = linearized(Color.blue(argb))
        val matrix = SRGB_TO_XYZ
        return floatArrayOf(
            matrix[0][0] * r + matrix[0][1] * g + matrix[0][2] * b,
            matrix[1][0] * r + matrix[1][1] * g + matrix[1][2] * b,
            matrix[2][0] * r + matrix[2][1] * g + matrix[2][2] * b
        )
    }

    fun yFromLstar(lstar: Float): Float {
        return if (lstar > 8.0f) {
            Math.pow((lstar.toDouble() + 16.0) / 116.0, 3.0).toFloat() * 100.0f
        } else lstar / 903.2963f * 100.0f
    }

    fun linearized(rgbComponent: Int): Float {
        val normalized = rgbComponent.toFloat() / 255.0f
        return if (normalized <= 0.04045f) {
            normalized / 12.92f * 100.0f
        } else Math.pow(
            ((0.055f + normalized) / 1.055f).toDouble(),
            2.4000000953674316
        )
            .toFloat() * 100.0f
    }
}