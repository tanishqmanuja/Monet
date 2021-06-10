package com.kyant.monet.nativecolor

import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

class Frame private constructor(
    val n: Float,
    val aw: Float,
    val nbb: Float,
    val ncb: Float,
    val c: Float,
    val nc: Float,
    val rgbD: FloatArray,
    val fl: Float,
    val flRoot: Float,
    val z: Float
) {
    companion object {
        val DEFAULT = make(
            CamUtils.WHITE_POINT_D65, (CamUtils.yFromLstar(50.0f)
                .toDouble() * 63.66197723675813 / 100.0).toFloat(), 50.0f, 2.0f, false
        )

        fun make(
            whitepoint: FloatArray?,
            adaptingLuminance: Float,
            backgroundLstar: Float,
            surround: Float,
            discountingIlluminant: Boolean
        ): Frame {
            val matrix = CamUtils.XYZ_TO_CAM16RGB
            val rW =
                whitepoint!![0] * matrix[0][0] + whitepoint[1] * matrix[0][1] + whitepoint[2] * matrix[0][2]
            val gW =
                whitepoint[0] * matrix[1][0] + whitepoint[1] * matrix[1][1] + whitepoint[2] * matrix[1][2]
            val bW =
                whitepoint[0] * matrix[2][0] + whitepoint[1] * matrix[2][1] + whitepoint[2] * matrix[2][2]
            val f = surround / 10.0f + 0.8f
            val c = if (f.toDouble() >= 0.9) MathUtils.lerp(
                0.59f,
                0.69f,
                (f - 0.9f) * 10.0f
            ) else MathUtils.lerp(0.525f, 0.59f, (f - 0.8f) * 10.0f)
            val d = if (discountingIlluminant) 1.0f else (1.0f - exp(
                ((-adaptingLuminance - 42.0f) / 92.0f).toDouble()
            )
                .toFloat() * 0.2777778f) * f
            val d2 = if (d.toDouble() > 1.0) 1.0f else if (d.toDouble() < 0.0) 0.0f else d
            val rgbD = floatArrayOf(
                100.0f / rW * d2 + 1.0f - d2,
                100.0f / gW * d2 + 1.0f - d2,
                100.0f / bW * d2 + 1.0f - d2
            )
            val k = 1.0f / (5.0f * adaptingLuminance + 1.0f)
            val k4 = k * k * k * k
            val k4F = 1.0f - k4
            val fl = k4 * adaptingLuminance + 0.1f * k4F * k4F * Math.cbrt(
                adaptingLuminance.toDouble() * 5.0
            )
                .toFloat()
            val n = CamUtils.yFromLstar(backgroundLstar) / whitepoint[1]
            val z = sqrt(n.toDouble()).toFloat() + 1.48f
            val nbb = 0.725f / n.toDouble().pow(0.2).toFloat()
            val rgbAFactors = floatArrayOf(
                ((rgbD[0] * fl * rW).toDouble() / 100.0).pow(0.42)
                    .toFloat(), ((rgbD[1] * fl * gW).toDouble() / 100.0).pow(0.42)
                    .toFloat(), ((rgbD[2] * fl * bW).toDouble() / 100.0).pow(0.42)
                    .toFloat()
            )
            val rgbA = floatArrayOf(
                rgbAFactors[0] * 400.0f / (rgbAFactors[0] + 27.13f),
                rgbAFactors[1] * 400.0f / (rgbAFactors[1] + 27.13f),
                rgbAFactors[2] * 400.0f / (rgbAFactors[2] + 27.13f)
            )
            return Frame(
                n,
                (rgbA[0] * 2.0f + rgbA[1] + rgbA[2] * 0.05f) * nbb,
                nbb,
                nbb,
                c,
                f,
                rgbD,
                fl,
                fl.toDouble().pow(0.25).toFloat(),
                z
            )
        }
    }
}