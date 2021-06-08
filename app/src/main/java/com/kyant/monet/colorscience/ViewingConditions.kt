package com.kyant.monet.colorscience

import com.kyant.monet.colorscience.CAT16.cat16OfWhite
import com.kyant.monet.math.lerp
import kotlin.math.E
import kotlin.math.pow
import kotlin.math.sqrt

class ViewingConditions(
    white: XYZ = Illuminant.D65,
    LA: Double = 40.0,
    Yb: Double = 18.0,
    s: Double = 2.0,
    discountingIlluminance: Boolean = false
) {
    val c: Double = if (s >= 1.0) {
        C2.lerp(C3, s - 1.0)
    } else {
        C1.lerp(C2, s)
    }
    val f: Double = if (c >= C2) {
        F2.lerp(F3, (c - C2) / (C3 - C2))
    } else {
        F1.lerp(F2, (c - C1) / (C2 - C1))
    }
    private val k = (5.0 * LA + 1.0).pow(-4.0)
    val fl: Double = k * LA + 0.1 * (1.0 - k).pow(2.0) * (5.0 * LA).pow(1.0 / 3.0)
    val n: Double = Yb / white[1]
    val z: Double = sqrt(n) + 1.48
    val nb: Double = if (n != 0.0) {
        0.725 * n.pow(-0.2)
    } else {
        0.0
    }
    private val d: Double = if (discountingIlluminance) {
        1.0
    } else {
        f * (1.0 - 1.0 / (3.6 * E.pow((LA + 42.0) / 92.0)))
    }

    private val cat16OfWhite = cat16OfWhite(white, d, fl)
    val wD = cat16OfWhite[0]

    private val wa = cat16OfWhite[1]
    val aw = 2.0 * wa[0] + wa[1] + wa[2] / 20.0

    companion object {
        const val C1: Double = 0.525
        const val C2: Double = 0.59
        const val C3: Double = 0.69

        const val F1: Double = 0.8
        const val F2: Double = 0.9
        const val F3: Double = 1.0
    }
}