package com.kyant.monet.colorscience

import com.kyant.monet.math.*
import kotlin.math.pow

typealias RGB = Matrix3x1
typealias XYZ = Matrix3x1

object SRGB {
    private val SRGB_COLOR_PRIMARIES = doubleArrayOf(0.64, 0.33, 0.3, 0.6, 0.15, 0.06)

    fun mRGB(white: XYZ, primaries: DoubleArray = SRGB_COLOR_PRIMARIES): Matrix3 {
        val (xr, yr, xg, yg, xb, yb) = primaries
        val pXYZ = matrix3Of(
            xr / yr,
            xg / yg,
            xb / yb,
            1.0,
            1.0,
            1.0,
            (1.0 - xr - yr) / yr,
            (1.0 - xg - yg) / yg,
            (1.0 - xb - yb) / yb
        )
        val sRGB: Matrix3x1 = pXYZ.inverse() * white

        val mRGB = zeroMatrix3Of()
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                mRGB[i][j] = sRGB[j] * pXYZ[i][j]
            }
        }
        return mRGB
    }

    private const val GAMMA: Double = 2.4
    private const val PHI: Double = 12.92
    private const val A: Double = 0.055
    private const val ALPHA: Double = 1.055
    private const val K0: Double = 0.040_448_236_277_107_6

    private fun linearized(t: Double): Double = if (t <= K0) {
        t / PHI
    } else {
        ((t + A) / ALPHA).pow(GAMMA)
    }

    private fun delinearized(t: Double): Double = if (t <= K0 / PHI) {
        t * PHI
    } else {
        ALPHA * (t).pow(1.0 / GAMMA) - A
    }

    fun RGB.toXYZ(param: CAM16Parameters = CAM16Parameters.Default): XYZ =
        param.mRGB * map { linearized(it) }.toDoubleArray()

    fun XYZ.tosRGB(param: CAM16Parameters = CAM16Parameters.Default): RGB =
        (param.mRGBInv * this).map { delinearized(it) }.toDoubleArray()
}