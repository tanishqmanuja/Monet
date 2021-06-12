package com.kyant.monet.nativecolor

internal object MathUtils {
    fun lerp(start: Double, stop: Double, amount: Double): Double {
        return (stop - start) * amount + start
    }
}