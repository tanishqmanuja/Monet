package com.kyant.nativemonet

object MathUtils {
    fun lerp(start: Float, stop: Float, amount: Float): Float {
        return (stop - start) * amount + start
    }
}