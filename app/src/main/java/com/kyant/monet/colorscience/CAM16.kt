package com.kyant.monet.colorscience

import androidx.compose.ui.graphics.Color
import com.kyant.monet.color.MonetColor.Companion.toRGB
import com.kyant.monet.colorscience.CAT16.cat16
import com.kyant.monet.colorscience.CAT16.inverseCat16
import com.kyant.monet.colorscience.SRGB.toXYZ
import com.kyant.monet.math.*
import kotlin.math.*
import kotlin.properties.Delegates

class CAM16(xyz: XYZ, param: CAM16Parameters = CAM16Parameters.Default) {
    private val ma = cat16(xyz, param)
    private val mCo = M_COEFFICIENTS * ma
    private val ac = mCo[0]
    private val a = mCo[1]
    private val b = mCo[2]
    private val p = ma[0] + ma[1] + 21.0 / 20.0 * ma[2]

    var j by Delegates.notNull<Double>()
    var q by Delegates.notNull<Double>()
    var h = 0.0
    var c = 0.0
    var m = 0.0
    var s = 0.0

    init {
        with(param.coefficients) {
            j = kJ * ac.pow(eJ)
            q = kQ * sqrt(j)

            if (a.absoluteValue > 0.007 && b.absoluteValue > 0.007) {
                h = b.atan2(a).toDegrees()
                if (h < 0.0) {
                    h += 360.0
                }
                c = kC * (sqrt(a * a + b * b) / (p + 0.305) * eccentricity(h)).pow(eC) * sqrt(j)
                m = kM * c
                s = ks * sqrt(m / q)
            }
        }
    }

    override fun toString(): String = "CAM16(J=$j, C=$c, h=$h, Q=$q, M=$m, s=$s)"

    companion object {
        fun fromRGB(rgb: RGB): CAM16 =
            CAM16(rgb.toXYZ(CAM16Parameters.Default), CAM16Parameters.Default)

        fun fromColor(color: Color): CAM16 = fromRGB(color.toRGB())

        val M_COEFFICIENTS = matrix3Of(
            2.0, 1.0, 1.0 / 20.0,
            1.0, -12.0 / 11.0, 1.0 / 11.0,
            1.0 / 9.0, 1.0 / 9.0, -2.0 / 9.0
        )

        data class CAM16Coefficients(
            val eJ: Double,
            val kJ: Double,
            val kQ: Double,
            val eC: Double,
            val kC: Double,
            val kM: Double,
            val ks: Double
        )

        fun coefficients(viewingConditions: ViewingConditions): CAM16Coefficients {
            val eJ = viewingConditions.c * viewingConditions.z
            val eC = 0.9
            val kM = viewingConditions.fl.pow(0.25)
            return CAM16Coefficients(
                eJ,
                kJ = 100.0 / viewingConditions.aw.pow(eJ),
                kQ = 0.4 * (viewingConditions.aw * viewingConditions.nb + 4.0) * kM
                        / viewingConditions.c,
                eC,
                kC = 0.1
                        * ((1.64 - 0.29.pow(viewingConditions.n)).pow(0.73))
                        * (50_000.0 / 13.0 * viewingConditions.f * viewingConditions.nb).pow(eC),
                kM,
                ks = 100.0
            )
        }

        private fun eccentricity(h: Double): Double = cos(h.toRadians() + 2.0) / 4.0 + 0.95

        private fun jch2xyz(JCh: Matrix3x1, param: CAM16Parameters): XYZ {
            val (J, C, h) = JCh
            with(param.coefficients) {
                val ac = (J / kJ).pow(1.0 / eJ)
                val a = (ac / param.viewingConditions.f + 0.305) /
                        ((kC * sqrt(J) / C).pow(1.0 / eC) * eccentricity(h)
                                / cos(h.toRadians())
                                + (108.0 / 23.0) * tan(h.toRadians())
                                + 11.0 / 23.0)
                val b = a * tan(h.toRadians())
                val ma = M_COEFFICIENTS.inverse() * matrix3x1Of(ac, a, b)
                return inverseCat16(ma, param)
            }
        }

        private fun binarySearchForYByJ(
            L: Double,
            C: Double,
            h: Double,
            param: CAM16Parameters
        ): Double {
            var d4 = 0.0
            var d5 = 100.0
            var d6 = -1.0
            var d7 = -1.0
            while ((d4 - d5).absoluteValue > 0.1) {
                val d8 = (d5 - d4) / 2.0 + d4
                val relativeLuminance = jch2xyz(matrix3x1Of(d8, C, h), param)[1]
                val abs = (relativeLuminance - L).absoluteValue
                if (d7 == -1.0 || abs < d7) {
                    d6 = d8
                    d7 = abs
                }
                if (relativeLuminance < L) {
                    d4 = d8
                } else {
                    d5 = d8
                }
            }
            return d6
        }

        private fun binarySearchForYByChroma(
            d: Double,
            d2: Double,
            d3: Double,
            d4: Double,
            d5: Double,
            param: CAM16Parameters
        ): Double {
            val z = jch2xyz(matrix3x1Of(d4, d2, d5), param)[1] >
                    jch2xyz(matrix3x1Of(d4, d3, d5), param)[1]
            var d6 = d3
            var d7 = 0.0
            var d8 = -1.0
            var d9 = d2
            while ((d9 - d6).absoluteValue > 0.1) {
                val d10 = (d6 - d9) / 2.0 + d9
                val relativeLuminance = jch2xyz(matrix3x1Of(d4, d10, d5), param)[1]
                val abs = (relativeLuminance - d).absoluteValue
                if (d8 == -1.0 || abs < d8) {
                    d7 = d10
                    d8 = abs
                }
                if (if (!z) relativeLuminance <= d else relativeLuminance > d) {
                    d9 = d10
                } else {
                    d6 = d10
                }
            }
            return d7
        }

        fun gamutMap(
            L: Double,
            C: Double,
            h: Double,
            param: CAM16Parameters
        ): Matrix3x1 {
            val yByJ = binarySearchForYByJ(L, C, h, param)
            val xyz = jch2xyz(matrix3x1Of(yByJ, C, h), param)
            val abs = (h - CAM16(xyz, param).h).absoluteValue
            val abs2 = (L - xyz[1]).absoluteValue
            if (abs <= 1.0 && abs2 <= 1.0) {
                return xyz
            }
            val yByChroma = binarySearchForYByChroma(L, 0.0, C, yByJ, h, param)
            val xyz2 = jch2xyz(
                matrix3x1Of(binarySearchForYByJ(L, yByChroma, h, param), yByChroma, h),
                param
            )
            val color = CAM16(xyz2, param)
            val abs3 = (h - color.h).absoluteValue
            val abs5 = (L - xyz2[1]).absoluteValue
            if ((abs2 <= 1.0 || abs5 > abs2) && (abs5 >= 1.0 && abs5 >= abs2 || abs3 >= abs)) {
                return xyz
            }
            return xyz2
        }
    }
}