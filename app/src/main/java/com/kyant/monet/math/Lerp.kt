package com.kyant.monet.math

fun Double.lerp(other: Double, fraction: Double): Double =
    (1.0 - fraction) * this + fraction * other