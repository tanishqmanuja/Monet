package com.kyant.monet.nativecolor

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class Frame private constructor(
    val n: Double,
    val aw: Double,
    val nbb: Double,
    val ncb: Double,
    val c: Double,
    val nc: Double,
    val rgbD: DoubleArray,
    val fl: Double,
    val flRoot: Double,
    val z: Double
) {
    companion object {
        val DEFAULT = make(
            CamUtils.WHITE_POINT_D65,
            Math.toDegrees(CamUtils.yFromLstar(50.0)) / 90.0,
            50.0,
            2.0,
            false
        )

        fun make(
            whitepoint: DoubleArray?,
            adaptingLuminance: Double,
            backgroundLstar: Double,
            surround: Double,
            discountingIlluminant: Boolean
        ): Frame {
            val matrix = CamUtils.XYZ_TO_CAM16RGB
            val rW =
                whitepoint!![0] * matrix[0][0] + whitepoint[1] * matrix[0][1] + whitepoint[2] * matrix[0][2]
            val gW =
                whitepoint[0] * matrix[1][0] + whitepoint[1] * matrix[1][1] + whitepoint[2] * matrix[1][2]
            val bW =
                whitepoint[0] * matrix[2][0] + whitepoint[1] * matrix[2][1] + whitepoint[2] * matrix[2][2]
            val f = surround / 10.0 + 0.8
            val c = if (f >= 0.9) MathUtils.lerp(0.59, 0.69, (f - 0.9) * 10.0)
            else MathUtils.lerp(0.525, 0.59, (f - 0.8) * 10.0)
            val d = if (discountingIlluminant) 1.0
            else (1.0 - exp(((-adaptingLuminance - 42.0) / 92.0)) * (1.0 / 3.6)) * f
            val d2 = if (d > 1.0) 1.0 else if (d < 0.0) 0.0 else d
            val rgbD = doubleArrayOf(
                100.0f / rW * d2 + 1.0 - d2,
                100.0f / gW * d2 + 1.0 - d2,
                100.0f / bW * d2 + 1.0 - d2
            )
            val k = 1.0 / (5.0 * adaptingLuminance + 1.0)
            val k4 = k * k * k * k
            val k4F = 1.0 - k4
            val fl = k4 * adaptingLuminance + 0.1 * k4F * k4F * Math.cbrt(adaptingLuminance * 5.0)
            val n = CamUtils.yFromLstar(backgroundLstar) / whitepoint[1]
            val z = sqrt(n) + 1.48
            val nbb = 0.725 / n.pow(0.2)
            val rgbAFactors = doubleArrayOf(
                ((rgbD[0] * fl * rW) / 100.0).pow(0.42),
                ((rgbD[1] * fl * gW) / 100.0).pow(0.42),
                ((rgbD[2] * fl * bW) / 100.0).pow(0.42)
            )
            val rgbA = doubleArrayOf(
                rgbAFactors[0] * 400.0 / (rgbAFactors[0] + 27.13),
                rgbAFactors[1] * 400.0 / (rgbAFactors[1] + 27.13),
                rgbAFactors[2] * 400.0 / (rgbAFactors[2] + 27.13)
            )
            return Frame(
                n,
                (rgbA[0] * 2.0 + rgbA[1] + rgbA[2] * 0.05) * nbb,
                nbb,
                nbb,
                c,
                f,
                rgbD,
                fl,
                fl.pow(0.25),
                z
            )
        }
    }
}