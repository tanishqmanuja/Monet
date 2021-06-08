package com.kyant.monet.colorscience

import kotlin.math.pow

object Contrast {
    const val SIGMA = 6.0 / 29.0

    fun Double.toLstar(): Double {
        val t = this / 100.0;
        return 116.0 * if (t > SIGMA.pow(3.0)) {
            t.pow(1.0 / 3.0)
        } else {
            t / (3.0 * SIGMA.pow(2.0)) + 4.0 / 29.0
        } - 16.0
    }

    fun Double.toY(): Double {
        val t = (this + 16.0) / 116.0;
        return 100.0 * if (t > SIGMA) {
            t.pow(3.0)
        } else {
            3.0 * SIGMA.pow(2.0) * (t - 4.0 / 29.0)
        }
    }
}
