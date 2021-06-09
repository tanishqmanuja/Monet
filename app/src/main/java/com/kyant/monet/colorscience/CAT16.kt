package com.kyant.monet.colorscience

import com.kyant.monet.math.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign

object CAT16 {
    val M16 = matrix3Of(
        0.401_288, 0.650_173, -0.051_461,
        -0.250_268, 1.204_414, 0.045_854,
        -0.002_079, 0.048_952, 0.953_127
    )

    private fun adapt(t: Double, factor: Double): Double =
        400.0 * t.sign * (1.0 - 27.13 / ((0.01 * factor * t.absoluteValue).pow(0.42) + 27.13))

    private fun unadapt(t: Double, factor: Double): Double =
        100.0 * t.sign / factor * (27.13 / (400.0 / t.absoluteValue - 1.0)).pow(1.0 / 0.42)

    fun cat16OfWhite(white: XYZ, d: Double, fl: Double): Array<DoubleArray> {
        val wRGB = M16 * white

        val wD = zeroMatrix3x1Of()
        for (i in 0 until 3) {
            wD[i] = 1.0.lerp(white[1] / wRGB[i], d)
        }

        val wa = zeroMatrix3x1Of()
        for (i in 0 until 3) {
            wa[i] = adapt(wD[i] * wRGB[i], fl)
        }

        return arrayOf(wD, wa)
    }

    fun cat16(xyz: XYZ, param: CAM16Parameters = CAM16Parameters.Default): Matrix3x1 {
        val mRGB = M16 * xyz
        val ma = zeroMatrix3x1Of()
        with(param.viewingConditions) {
            for (i in 0 until 3) {
                ma[i] = adapt(wD[i] * mRGB[i], fl)
            }
            return ma
        }
    }

    fun inverseCat16(ma: Matrix3x1, param: CAM16Parameters = CAM16Parameters.Default): XYZ {
        val rgb = zeroMatrix3x1Of()
        with(param.viewingConditions) {
            for (i in 0 until 3) {
                rgb[i] = unadapt(ma[i], fl) / wD[i]
            }
            return param.m16Inv * rgb
        }
    }
}