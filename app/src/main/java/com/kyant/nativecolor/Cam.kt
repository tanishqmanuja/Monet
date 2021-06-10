package com.kyant.nativecolor

import kotlin.math.*

class Cam internal constructor(
    val hue: Float,
    val chroma: Float,
    val j: Float,
    val q: Float,
    val m: Float,
    val s: Float,
    val jstar: Float,
    val astar: Float,
    val bstar: Float
) {
    fun distance(other: Cam): Float {
        val dJ = jstar - other.jstar
        val dA = astar - other.astar
        val dB = bstar - other.bstar
        return (sqrt((dJ * dJ + dA * dA + dB * dB).toDouble()).pow(0.63) * 1.41).toFloat()
    }

    fun viewedInSrgb(): Int {
        return viewed(Frame.DEFAULT)
    }

    /* JADX INFO: Multiple debug info for r0v9 float: [D('rCBase' float), D('z' float)] */
    fun viewed(frame: Frame): Int {
        val alpha = if (chroma.toDouble() == 0.0 || j.toDouble() == 0.0) {
            0.0f
        } else {
            chroma / sqrt(j.toDouble() / 100.0).toFloat()
        }
        val t = (alpha.toDouble() / (1.64 - 0.29.pow(frame.n.toDouble())).pow(0.73)).pow(
            1.1111111111111112
        ).toFloat()
        val hRad = hue * 3.1415927f / 180.0f
        val ac =
            frame.aw * (j.toDouble() / 100.0).pow(1.0 / frame.c.toDouble() / frame.z.toDouble())
                .toFloat()
        val p1 =
            3846.1538f * (cos(hRad.toDouble() + 2.0) + 3.8).toFloat() * 0.25f * frame.nc * frame.ncb
        val p2 = ac / frame.nbb
        val hSin = sin(hRad.toDouble()).toFloat()
        val hCos = cos(hRad.toDouble()).toFloat()
        val gamma = (0.305f + p2) * 23.0f * t / (23.0f * p1 + 11.0f * t * hCos + 108.0f * t * hSin)
        val a = gamma * hCos
        val b = gamma * hSin
        val rA = (p2 * 460.0f + 451.0f * a + 288.0f * b) / 1403.0f
        val gA = (p2 * 460.0f - 891.0f * a - 261.0f * b) / 1403.0f
        val bA = (460.0f * p2 - 220.0f * a - 6300.0f * b) / 1403.0f
        val rC = sign(rA) * (100.0f / frame.fl) * max(
            0.0,
            abs(rA).toDouble() * 27.13 / (400.0 - abs(rA)
                .toDouble())
        ).toFloat().toDouble().pow(2.380952380952381).toFloat()
        val gC = sign(gA) * (100.0f / frame.fl) * max(
            0.0,
            abs(gA).toDouble() * 27.13 / (400.0 - abs(gA)
                .toDouble())
        ).toFloat().toDouble().pow(2.380952380952381).toFloat()
        val bC = sign(bA) * (100.0f / frame.fl) * max(
            0.0,
            abs(bA).toDouble() * 27.13 / (400.0 - abs(bA)
                .toDouble())
        ).toFloat().toDouble().pow(2.380952380952381).toFloat()
        val rF = rC / frame.rgbD[0]
        val gF = gC / frame.rgbD[1]
        val bF = bC / frame.rgbD[2]
        val matrix = CamUtils.CAM16RGB_TO_XYZ
        return ColorUtils.XYZToColor(
            (matrix[0][0] * rF + matrix[0][1] * gF + matrix[0][2] * bF).toDouble(),
            (matrix[1][0] * rF + matrix[1][1] * gF + matrix[1][2] * bF).toDouble(),
            (matrix[2][0] * rF + matrix[2][1] * gF + matrix[2][2] * bF).toDouble()
        )
    }

    companion object {
        private const val CHROMA_SEARCH_ENDPOINT = 0.4f
        private const val DE_MAX = 1.0f
        private const val DL_MAX = 0.2f
        private const val LIGHTNESS_SEARCH_ENDPOINT = 0.01f
        fun getInt(hue: Float, chroma: Float, lstar: Float): Int {
            return getInt(hue, chroma, lstar, Frame.DEFAULT)
        }

        fun fromInt(argb: Int): Cam {
            return fromIntInFrame(argb, Frame.DEFAULT)
        }

        fun fromIntInFrame(argb: Int, frame: Frame): Cam {
            val hue: Float
            val xyz = CamUtils.xyzFromInt(argb)
            val matrix = CamUtils.XYZ_TO_CAM16RGB
            val rT = xyz[0] * matrix[0][0] + xyz[1] * matrix[0][1] + xyz[2] * matrix[0][2]
            val gT = xyz[0] * matrix[1][0] + xyz[1] * matrix[1][1] + xyz[2] * matrix[1][2]
            val bT = xyz[0] * matrix[2][0] + xyz[1] * matrix[2][1] + xyz[2] * matrix[2][2]
            val rD = frame.rgbD[0] * rT
            val gD = frame.rgbD[1] * gT
            val bD = frame.rgbD[2] * bT
            val rAF = ((frame.fl * abs(rD)).toDouble() / 100.0).pow(0.42)
                .toFloat()
            val gAF = ((frame.fl * abs(gD)).toDouble() / 100.0).pow(0.42)
                .toFloat()
            val bAF = ((frame.fl * abs(bD)).toDouble() / 100.0).pow(0.42)
                .toFloat()
            val rA = sign(rD) * 400.0f * rAF / (rAF + 27.13f)
            val gA = sign(gD) * 400.0f * gAF / (gAF + 27.13f)
            val bA = sign(bD) * 400.0f * bAF / (27.13f + bAF)
            val a = (rA.toDouble() * 11.0 + gA.toDouble() * -12.0 + bA.toDouble()).toFloat() / 11.0f
            val b = ((rA + gA).toDouble() - bA.toDouble() * 2.0).toFloat() / 9.0f
            val u = (rA * 20.0f + gA * 20.0f + 21.0f * bA) / 20.0f
            val p2 = (40.0f * rA + gA * 20.0f + bA) / 20.0f
            val atanDegrees = atan2(b.toDouble(), a.toDouble())
                .toFloat() * 180.0f / 3.1415927f
            hue = if (atanDegrees < 0.0f) {
                atanDegrees + 360.0f
            } else {
                if (atanDegrees >= 360.0f) atanDegrees - 360.0f else atanDegrees
            }
            val hueRadians = hue * 3.1415927f / 180.0f
            val j = (frame.nbb * p2 / frame.aw).toDouble().pow((frame.c * frame.z).toDouble())
                .toFloat() * 100.0f
            val q = 4.0f / frame.c * sqrt((j / 100.0f).toDouble())
                .toFloat() * (frame.aw + 4.0f) * frame.flRoot
            val alpha = (sqrt((a * a + b * b).toDouble())
                .toFloat() * (3846.1538f * ((cos((if (hue.toDouble() < 20.14) hue + 360.0f else hue).toDouble() * 3.141592653589793 / 180.0 + 2.0) + 3.8).toFloat() * 0.25f) * frame.nc * frame.ncb) / (0.305f + u)).toDouble()
                .pow(0.9).toFloat() * (1.64 - 0.29.pow(frame.n.toDouble())).pow(0.73)
                .toFloat()
            val c = sqrt(j.toDouble() / 100.0).toFloat() * alpha
            val m = frame.flRoot * c
            val jstar = 1.7f * j / (0.007f * j + 1.0f)
            val mstar = ln((0.0228f * m + 1.0f).toDouble())
                .toFloat() * 43.85965f
            return Cam(
                hue,
                c,
                j,
                q,
                m,
                sqrt((frame.c * alpha / (frame.aw + 4.0f)).toDouble())
                    .toFloat() * 50.0f,
                jstar,
                cos(hueRadians.toDouble()).toFloat() * mstar,
                sin(
                    hueRadians.toDouble()
                ).toFloat() * mstar
            )
        }

        private fun fromJch(j: Float, c: Float, h: Float): Cam {
            return fromJchInFrame(j, c, h, Frame.DEFAULT)
        }

        private fun fromJchInFrame(j: Float, c: Float, h: Float, frame: Frame): Cam {
            val q = 4.0f / frame.c * sqrt(j.toDouble() / 100.0)
                .toFloat() * (frame.aw + 4.0f) * frame.flRoot
            val m = c * frame.flRoot
            val s = sqrt(
                (frame.c * (c / sqrt(j.toDouble() / 100.0)
                    .toFloat()) / (frame.aw + 4.0f)).toDouble()
            ).toFloat() * 50.0f
            val hueRadians = 3.1415927f * h / 180.0f
            val jstar = 1.7f * j / (0.007f * j + 1.0f)
            val mstar = ln(m.toDouble() * 0.0228 + 1.0)
                .toFloat() * 43.85965f
            return Cam(
                h, c, j, q, m, s, jstar, mstar * cos(hueRadians.toDouble())
                    .toFloat(), mstar * sin(hueRadians.toDouble()).toFloat()
            )
        }

        fun getInt(hue: Float, chroma: Float, lstar: Float, frame: Frame): Int {
            if (chroma.toDouble() < 1.0 || lstar.roundToInt()
                    .toDouble() <= 0.0 || lstar.roundToInt().toDouble() >= 100.0
            ) {
                return CamUtils.intFromLstar(lstar)
            }
            var hue2 = 0.0f
            if (hue >= 0.0f) {
                hue2 = min(360.0f, hue)
            }
            var high = chroma
            var mid = chroma
            var low = 0.0f
            var isFirstLoop = true
            var answer: Cam? = null
            while (abs(low - high) >= CHROMA_SEARCH_ENDPOINT) {
                val possibleAnswer = findCamByJ(hue2, mid, lstar)
                if (!isFirstLoop) {
                    if (possibleAnswer == null) {
                        high = mid
                    } else {
                        answer = possibleAnswer
                        low = mid
                    }
                    mid = low + (high - low) / 2.0f
                } else if (possibleAnswer != null) {
                    return possibleAnswer.viewed(frame)
                } else {
                    isFirstLoop = false
                    mid = low + (high - low) / 2.0f
                }
            }
            return answer?.viewed(frame) ?: CamUtils.intFromLstar(lstar)
        }

        private fun findCamByJ(hue: Float, chroma: Float, lstar: Float): Cam? {
            var low = 0.0f
            var high = 100.0f
            var bestdL = 1000.0f
            var bestdE = 1000.0f
            var bestCam: Cam? = null
            while (abs(low - high) > LIGHTNESS_SEARCH_ENDPOINT) {
                val mid = low + (high - low) / 2.0f
                val clipped = fromJch(mid, chroma, hue).viewedInSrgb()
                val clippedLstar = CamUtils.lstarFromInt(clipped)
                val dL = abs(lstar - clippedLstar)
                if (dL < DL_MAX) {
                    val camClipped = fromInt(clipped)
                    val dE = camClipped.distance(fromJch(camClipped.j, camClipped.chroma, hue))
                    if (dE <= DE_MAX) {
                        bestdL = dL
                        bestdE = dE
                        bestCam = camClipped
                    }
                }
                if (bestdL == 0.0f && bestdE == 0.0f) {
                    break
                } else if (clippedLstar < lstar) {
                    low = mid
                } else {
                    high = mid
                }
            }
            return bestCam
        }
    }
}