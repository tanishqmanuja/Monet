package com.kyant.monet.colorscience

import com.kyant.monet.math.Matrix3
import com.kyant.monet.math.Matrix3x1
import com.kyant.monet.math.inverse

data class CAM16Parameters(
    val white: Matrix3x1 = Illuminant.D65,
    val mRGB: Matrix3 = SRGB.mRGB(white),
    val mRGBInv: Matrix3 = mRGB.inverse(),
    val m16Inv: Matrix3 = CAT16.M16.inverse(),
    val viewingConditions: ViewingConditions = ViewingConditions(),
    val coefficients: CAM16.Companion.CAM16Coefficients = CAM16.coefficients(viewingConditions)
)