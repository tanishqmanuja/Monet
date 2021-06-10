package com.kyant.monet.nativecolor

import android.graphics.Color
import kotlin.math.*

object ColorUtils {
    private const val MIN_ALPHA_SEARCH_MAX_ITERATIONS = 10
    private const val MIN_ALPHA_SEARCH_PRECISION = 1
    private val TEMP_ARRAY = ThreadLocal<DoubleArray>()
    private const val XYZ_EPSILON = 0.008856
    private const val XYZ_KAPPA = 903.3
    private const val XYZ_WHITE_REFERENCE_X = 95.047
    private const val XYZ_WHITE_REFERENCE_Y = 100.0
    private const val XYZ_WHITE_REFERENCE_Z = 108.883
    fun compositeColors(foreground: Int, background: Int): Int {
        val bgAlpha = Color.alpha(background)
        val fgAlpha = Color.alpha(foreground)
        val a = compositeAlpha(fgAlpha, bgAlpha)
        return Color.argb(
            a,
            compositeComponent(Color.red(foreground), fgAlpha, Color.red(background), bgAlpha, a),
            compositeComponent(
                Color.green(foreground), fgAlpha, Color.green(background), bgAlpha, a
            ),
            compositeComponent(
                Color.blue(foreground), fgAlpha, Color.blue(background), bgAlpha, a
            )
        )
    }

    private fun compositeAlpha(foregroundAlpha: Int, backgroundAlpha: Int): Int {
        return 255 - (255 - backgroundAlpha) * (255 - foregroundAlpha) / 255
    }

    private fun compositeComponent(fgC: Int, fgA: Int, bgC: Int, bgA: Int, a: Int): Int {
        return if (a == 0) {
            0
        } else (fgC * 255 * fgA + bgC * bgA * (255 - fgA)) / (a * 255)
    }

    fun calculateLuminance(color: Int): Double {
        val result = tempDouble3Array
        colorToXYZ(color, result)
        return result[1] / XYZ_WHITE_REFERENCE_Y
    }

    fun calculateContrast(foreground: Int, background: Int): Double {
        var foreground = foreground
        if (Color.alpha(background) == 255) {
            if (Color.alpha(foreground) < 255) {
                foreground = compositeColors(foreground, background)
            }
            val luminance1 = calculateLuminance(foreground) + 0.05
            val luminance2 = calculateLuminance(background) + 0.05
            return max(luminance1, luminance2) / min(luminance1, luminance2)
        }
        throw IllegalArgumentException(
            "background can not be translucent: #" + Integer.toHexString(
                background
            )
        )
    }

    private fun binaryAlphaSearch(
        foreground: Int,
        background: Int,
        minContrastRatio: Float,
        calculator: ContrastCalculator
    ): Int {
        var minAlpha = 0
        var maxAlpha = 255
        var numIterations = 0
        while (numIterations <= 10 && maxAlpha - minAlpha > 1) {
            val testAlpha = (minAlpha + maxAlpha) / 2
            if (calculator.calculateContrast(
                    foreground,
                    background,
                    testAlpha
                ) < minContrastRatio.toDouble()
            ) {
                minAlpha = testAlpha
            } else {
                maxAlpha = testAlpha
            }
            numIterations++
        }
        return maxAlpha
    }

    fun RGBToHSL(r: Int, g: Int, b: Int, outHsl: FloatArray) {
        val h: Float
        val s: Float
        val rf = r.toFloat() / 255.0f
        val gf = g.toFloat() / 255.0f
        val bf = b.toFloat() / 255.0f
        val max = max(rf, max(gf, bf))
        val min = min(rf, min(gf, bf))
        val deltaMaxMin = max - min
        val l = (max + min) / 2.0f
        if (max == min) {
            s = 0.0f
            h = 0.0f
        } else {
            h = when (max) {
                rf -> {
                    (gf - bf) / deltaMaxMin % 6.0f
                }
                gf -> {
                    (bf - rf) / deltaMaxMin + 2.0f
                }
                else -> {
                    (rf - gf) / deltaMaxMin + 4.0f
                }
            }
            s = deltaMaxMin / (1.0f - abs(2.0f * l - 1.0f))
        }
        var h2 = 60.0f * h % 360.0f
        if (h2 < 0.0f) {
            h2 += 360.0f
        }
        outHsl[0] = constrain(h2, 0.0f, 360.0f)
        outHsl[1] = constrain(s, 0.0f, 1.0f)
        outHsl[2] = constrain(l, 0.0f, 1.0f)
    }

    fun colorToHSL(color: Int, outHsl: FloatArray) {
        RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), outHsl)
    }

    fun HSLToColor(hsl: FloatArray): Int {
        val h = hsl[0]
        val s = hsl[1]
        val l = hsl[2]
        val c = (1.0f - abs(l * 2.0f - 1.0f)) * s
        val m = l - 0.5f * c
        val x = (1.0f - abs(h / 60.0f % 2.0f - 1.0f)) * c
        var r = 0
        var g = 0
        var b = 0
        when (h.toInt() / 60) {
            0 -> {
                r = ((c + m) * 255.0f).roundToInt()
                g = ((x + m) * 255.0f).roundToInt()
                b = (255.0f * m).roundToInt()
            }
            1 -> {
                r = ((x + m) * 255.0f).roundToInt()
                g = ((c + m) * 255.0f).roundToInt()
                b = (255.0f * m).roundToInt()
            }
            2 -> {
                r = (m * 255.0f).roundToInt()
                g = ((c + m) * 255.0f).roundToInt()
                b = ((x + m) * 255.0f).roundToInt()
            }
            3 -> {
                r = (m * 255.0f).roundToInt()
                g = ((x + m) * 255.0f).roundToInt()
                b = ((c + m) * 255.0f).roundToInt()
            }
            4 -> {
                r = ((x + m) * 255.0f).roundToInt()
                g = (m * 255.0f).roundToInt()
                b = ((c + m) * 255.0f).roundToInt()
            }
            5, 6 -> {
                r = ((c + m) * 255.0f).roundToInt()
                g = (m * 255.0f).roundToInt()
                b = ((x + m) * 255.0f).roundToInt()
            }
        }
        return Color.rgb(constrain(r, 0, 255), constrain(g, 0, 255), constrain(b, 0, 255))
    }

    fun colorToCAM(color: Int): Cam {
        return Cam.fromInt(color)
    }

    fun CAMToColor(hue: Float, chroma: Float, lstar: Float): Int {
        return Cam.getInt(hue, chroma, lstar)
    }

    fun setAlphaComponent(color: Int, alpha: Int): Int {
        if (alpha in 0..255) {
            return 16777215 and color or (alpha shl 24)
        }
        throw IllegalArgumentException("alpha must be between 0 and 255.")
    }

    fun colorToLAB(color: Int, outLab: DoubleArray) {
        RGBToLAB(Color.red(color), Color.green(color), Color.blue(color), outLab)
    }

    fun RGBToLAB(r: Int, g: Int, b: Int, outLab: DoubleArray) {
        RGBToXYZ(r, g, b, outLab)
        XYZToLAB(outLab[0], outLab[1], outLab[2], outLab)
    }

    fun colorToXYZ(color: Int, outXyz: DoubleArray) {
        RGBToXYZ(Color.red(color), Color.green(color), Color.blue(color), outXyz)
    }

    fun RGBToXYZ(r: Int, g: Int, b: Int, outXyz: DoubleArray) {
        if (outXyz.size == 3) {
            val sr = r.toDouble() / 255.0
            val sr2 = if (sr < 0.04045) sr / 12.92 else ((sr + 0.055) / 1.055).pow(2.4)
            val sg = g.toDouble() / 255.0
            val sg2 = if (sg < 0.04045) sg / 12.92 else ((sg + 0.055) / 1.055).pow(2.4)
            val sb = b.toDouble() / 255.0
            val sb2 = if (sb < 0.04045) sb / 12.92 else ((0.055 + sb) / 1.055).pow(2.4)
            outXyz[0] = (0.4124 * sr2 + 0.3576 * sg2 + 0.1805 * sb2) * XYZ_WHITE_REFERENCE_Y
            outXyz[1] = (0.2126 * sr2 + 0.7152 * sg2 + 0.0722 * sb2) * XYZ_WHITE_REFERENCE_Y
            outXyz[2] = (0.0193 * sr2 + 0.1192 * sg2 + 0.9505 * sb2) * XYZ_WHITE_REFERENCE_Y
            return
        }
        throw IllegalArgumentException("outXyz must have a length of 3.")
    }

    fun XYZToLAB(x: Double, y: Double, z: Double, outLab: DoubleArray) {
        if (outLab.size == 3) {
            val x2 = pivotXyzComponent(x / XYZ_WHITE_REFERENCE_X)
            val y2 = pivotXyzComponent(y / XYZ_WHITE_REFERENCE_Y)
            val z2 = pivotXyzComponent(z / XYZ_WHITE_REFERENCE_Z)
            outLab[0] = max(0.0, 116.0 * y2 - 16.0)
            outLab[1] = (x2 - y2) * 500.0
            outLab[2] = (y2 - z2) * 200.0
            return
        }
        throw IllegalArgumentException("outLab must have a length of 3.")
    }

    fun LABToXYZ(l: Double, a: Double, b: Double, outXyz: DoubleArray) {
        val fy = (l + 16.0) / 116.0
        val fx = a / 500.0 + fy
        val fz = fy - b / 200.0
        val tmp = fx.pow(3.0)
        val xr = if (tmp > XYZ_EPSILON) tmp else (fx * 116.0 - 16.0) / XYZ_KAPPA
        val yr = if (l > 7.9996247999999985) fy.pow(3.0) else l / XYZ_KAPPA
        val tmp2 = fz.pow(3.0)
        val zr = if (tmp2 > XYZ_EPSILON) tmp2 else (116.0 * fz - 16.0) / XYZ_KAPPA
        outXyz[0] = XYZ_WHITE_REFERENCE_X * xr
        outXyz[1] = XYZ_WHITE_REFERENCE_Y * yr
        outXyz[2] = XYZ_WHITE_REFERENCE_Z * zr
    }

    fun XYZToColor(x: Double, y: Double, z: Double): Int {
        val r = (3.2406 * x + -1.5372 * y + -0.4986 * z) / XYZ_WHITE_REFERENCE_Y
        val g = (-0.9689 * x + 1.8758 * y + 0.0415 * z) / XYZ_WHITE_REFERENCE_Y
        val b = (0.0557 * x + -0.204 * y + 1.057 * z) / XYZ_WHITE_REFERENCE_Y
        return Color.rgb(
            constrain(
                ((if (r > 0.0031308) r.pow(0.4166666666666667) * 1.055 - 0.055 else r * 12.92) * 255.0).roundToInt(),
                0,
                255
            ), constrain(
                ((if (g > 0.0031308) g.pow(0.4166666666666667) * 1.055 - 0.055 else g * 12.92) * 255.0).roundToInt(),
                0,
                255
            ), constrain(
                (255.0 * if (b > 0.0031308) b.pow(0.4166666666666667) * 1.055 - 0.055 else b * 12.92).roundToInt(),
                0,
                255
            )
        )
    }

    fun LABToColor(l: Double, a: Double, b: Double): Int {
        val result = tempDouble3Array
        LABToXYZ(l, a, b, result)
        return XYZToColor(result[0], result[1], result[2])
    }

    fun distanceEuclidean(labX: DoubleArray, labY: DoubleArray): Double {
        return sqrt(
            (labX[0] - labY[0]).pow(2.0) + (labX[1] - labY[1]).pow(2.0) + (labX[2] - labY[2]).pow(
                2.0
            )
        )
    }

    private fun constrain(amount: Float, low: Float, high: Float): Float {
        if (amount < low) {
            return low
        }
        return if (amount > high) high else amount
    }

    private fun constrain(amount: Int, low: Int, high: Int): Int {
        if (amount < low) {
            return low
        }
        return if (amount > high) high else amount
    }

    private fun pivotXyzComponent(component: Double): Double {
        return if (component > XYZ_EPSILON) {
            Math.pow(component, 0.3333333333333333)
        } else (XYZ_KAPPA * component + 16.0) / 116.0
    }

    fun blendARGB(color1: Int, color2: Int, ratio: Float): Int {
        val inverseRatio = 1.0f - ratio
        return Color.argb(
            (Color.alpha(color1)
                .toFloat() * inverseRatio + Color.alpha(color2)
                .toFloat() * ratio).toInt(), (Color.red(color1)
                .toFloat() * inverseRatio + Color.red(
                color2
            ).toFloat() * ratio).toInt(), (Color.green(color1)
                .toFloat() * inverseRatio + Color.green(color2)
                .toFloat() * ratio).toInt(), (Color.blue(color1)
                .toFloat() * inverseRatio + Color.blue(color2)
                .toFloat() * ratio).toInt()
        )
    }

    fun blendHSL(hsl1: FloatArray, hsl2: FloatArray, ratio: Float, outResult: FloatArray) {
        if (outResult.size == 3) {
            val inverseRatio = 1.0f - ratio
            outResult[0] = circularInterpolate(hsl1[0], hsl2[0], ratio)
            outResult[1] = hsl1[1] * inverseRatio + hsl2[1] * ratio
            outResult[2] = hsl1[2] * inverseRatio + hsl2[2] * ratio
            return
        }
        throw IllegalArgumentException("result must have a length of 3.")
    }

    fun blendLAB(lab1: DoubleArray, lab2: DoubleArray, ratio: Double, outResult: DoubleArray) {
        if (outResult.size == 3) {
            val inverseRatio = 1.0 - ratio
            outResult[0] = lab1[0] * inverseRatio + lab2[0] * ratio
            outResult[1] = lab1[1] * inverseRatio + lab2[1] * ratio
            outResult[2] = lab1[2] * inverseRatio + lab2[2] * ratio
            return
        }
        throw IllegalArgumentException("outResult must have a length of 3.")
    }

    fun circularInterpolate(a: Float, b: Float, f: Float): Float {
        var a = a
        var b = b
        if (abs(b - a) > 180.0f) {
            if (b > a) {
                a += 360.0f
            } else {
                b += 360.0f
            }
        }
        return ((b - a) * f + a) % 360.0f
    }

    private val tempDouble3Array: DoubleArray
        get() {
            val threadLocal = TEMP_ARRAY
            val result = threadLocal.get()
            if (result != null) {
                return result
            }
            val result2 = DoubleArray(3)
            threadLocal.set(result2)
            return result2
        }

    /* access modifiers changed from: private */
    interface ContrastCalculator {
        fun calculateContrast(i: Int, i2: Int, i3: Int): Double
    }
}