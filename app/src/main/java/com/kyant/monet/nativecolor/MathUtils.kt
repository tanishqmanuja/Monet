package com.kyant.monet.nativecolor

object MathUtils {
    fun lerp(start: Float, stop: Float, amount: Float): Float {
        return (stop - start) * amount + start
    }
}