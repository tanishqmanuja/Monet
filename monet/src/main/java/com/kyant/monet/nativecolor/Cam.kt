package com.kyant.monet.nativecolor

import kotlin.math.*

internal class Cam internal constructor(
    val hue: Double,
    val chroma: Double,
    val j: Double,
    val q: Double,
    val m: Double,
    val s: Double,
    val jstar: Double,
    val astar: Double,
    val bstar: Double
) {
    fun distance(other: Cam): Double {
        val dJ = jstar - other.jstar
        val dA = astar - other.astar
        val dB = bstar - other.bstar
        return sqrt(dJ * dJ + dA * dA + dB * dB).pow(0.63) * 1.41
    }

    fun viewedInSrgb(): Int {
        return viewed(Frame.DEFAULT)
    }

    fun viewed(frame: Frame): Int {
        val alpha = if (chroma == 0.0 || j == 0.0) {
            0.0
        } else {
            chroma / sqrt(j / 100.0)
        }
        val t = (alpha / (1.64 - 0.29.pow(frame.n)).pow(0.73)).pow(10.0 / 9.0)
        val hRad = hue * PI / 180.0
        val ac = frame.aw * (j / 100.0).pow(1.0 / frame.c / frame.z)
        val p1 = (50000.0 / 13.0) * (cos(hRad + 2.0) + 3.8) * 0.25f * frame.nc * frame.ncb
        val p2 = ac / frame.nbb
        val hSin = sin(hRad)
        val hCos = cos(hRad)
        val gamma = (0.305 + p2) * 23.0 * t / (23.0 * p1 + 11.0 * t * hCos + 108.0 * t * hSin)
        val a = gamma * hCos
        val b = gamma * hSin
        val rA = (p2 * 460.0 + 451.0 * a + 288.0 * b) / 1403.0
        val gA = (p2 * 460.0 - 891.0 * a - 261.0 * b) / 1403.0
        val bA = (460.0 * p2 - 220.0 * a - 6300.0 * b) / 1403.0
        val rC = sign(rA) * (100.0 / frame.fl) * max(0.0, abs(rA) * 27.13 / (400.0 - abs(rA)))
            .pow(1.0 / 0.42)
        val gC = sign(gA) * (100.0 / frame.fl) * max(0.0, abs(gA) * 27.13 / (400.0 - abs(gA)))
            .pow(1.0 / 0.42)
        val bC = sign(bA) * (100.0 / frame.fl) * max(0.0, abs(bA) * 27.13 / (400.0 - abs(bA)))
            .pow(1.0 / 0.42)
        val rF = rC / frame.rgbD[0]
        val gF = gC / frame.rgbD[1]
        val bF = bC / frame.rgbD[2]
        val matrix = CamUtils.CAM16RGB_TO_XYZ
        return ColorUtils.XYZToColor(
            (matrix[0][0] * rF + matrix[0][1] * gF + matrix[0][2] * bF),
            (matrix[1][0] * rF + matrix[1][1] * gF + matrix[1][2] * bF),
            (matrix[2][0] * rF + matrix[2][1] * gF + matrix[2][2] * bF)
        )
    }

    companion object {
        private const val CHROMA_SEARCH_ENDPOINT = 0.4
        private const val DE_MAX = 1.0
        private const val DL_MAX = 0.2
        private const val LIGHTNESS_SEARCH_ENDPOINT = 0.01

        fun getInt(hue: Double, chroma: Double, lstar: Double): Int {
            return getInt(hue, chroma, lstar, Frame.DEFAULT)
        }

        fun fromInt(argb: Int): Cam {
            return fromIntInFrame(argb, Frame.DEFAULT)
        }

        fun fromIntInFrame(argb: Int, frame: Frame): Cam {
            val hue: Double
            val xyz = CamUtils.xyzFromInt(argb)
            val matrix = CamUtils.XYZ_TO_CAM16RGB
            val rT = xyz[0] * matrix[0][0] + xyz[1] * matrix[0][1] + xyz[2] * matrix[0][2]
            val gT = xyz[0] * matrix[1][0] + xyz[1] * matrix[1][1] + xyz[2] * matrix[1][2]
            val bT = xyz[0] * matrix[2][0] + xyz[1] * matrix[2][1] + xyz[2] * matrix[2][2]
            val rD = frame.rgbD[0] * rT
            val gD = frame.rgbD[1] * gT
            val bD = frame.rgbD[2] * bT
            val rAF = ((frame.fl * abs(rD)) / 100.0).pow(0.42)
            val gAF = ((frame.fl * abs(gD)) / 100.0).pow(0.42)
            val bAF = ((frame.fl * abs(bD)) / 100.0).pow(0.42)
            val rA = sign(rD) * 400.0 * rAF / (rAF + 27.13)
            val gA = sign(gD) * 400.0 * gAF / (gAF + 27.13)
            val bA = sign(bD) * 400.0 * bAF / (bAF + 27.13)
            val a = (rA * 11.0 + gA * -12.0 + bA) / 11.0
            val b = ((rA + gA) - bA * 2.0) / 9.0
            val u = (rA * 20.0 + gA * 20.0 + 21.0 * bA) / 20.0
            val p2 = (40.0 * rA + gA * 20.0 + bA) / 20.0
            val atanDegrees = Math.toDegrees(atan2(b, a))
            hue = if (atanDegrees < 0.0) {
                atanDegrees + 360.0
            } else {
                if (atanDegrees >= 360.0) atanDegrees - 360.0 else atanDegrees
            }
            val hueRadians = Math.toRadians(hue)
            val j = (frame.nbb * p2 / frame.aw).pow((frame.c * frame.z)) * 100.0
            val q = 4.0 / frame.c * sqrt((j / 100.0)) * (frame.aw + 4.0) * frame.flRoot
            val alpha = (sqrt((a * a + b * b))
                    * ((50000.0 / 13.0) * ((cos((if (hue < 20.14) hue + 360.0 else hue) * PI / 180.0 + 2.0) + 3.8) * 0.25) * frame.nc * frame.ncb) / (0.305 + u))
                .pow(0.9) * (1.64 - 0.29.pow(frame.n)).pow(0.73)
            val c = sqrt(j / 100.0) * alpha
            val m = frame.flRoot * c
            val jstar = 1.7 * j / (0.007 * j + 1.0)
            val mstar = ln((0.0228 * m + 1.0)) * (1.0 / 0.0228)
            return Cam(
                hue, c, j, q, m,
                sqrt((frame.c * alpha / (frame.aw + 4.0))) * 50.0,
                jstar,
                cos(hueRadians) * mstar,
                sin(hueRadians) * mstar
            )
        }

        private fun fromJch(j: Double, c: Double, h: Double): Cam {
            return fromJchInFrame(j, c, h, Frame.DEFAULT)
        }

        private fun fromJchInFrame(j: Double, c: Double, h: Double, frame: Frame): Cam {
            val q = 4.0 / frame.c * sqrt(j / 100.0) * (frame.aw + 4.0) * frame.flRoot
            val m = c * frame.flRoot
            val s = sqrt((frame.c * (c / sqrt(j / 100.0)) / (frame.aw + 4.0))) * 50.0
            val hueRadians = Math.toRadians(h)
            val jstar = 1.7 * j / (0.007 * j + 1.0)
            val mstar = ln(m * 0.0228 + 1.0) * (1.0 / 0.0228)
            return Cam(h, c, j, q, m, s, jstar, mstar * cos(hueRadians), mstar * sin(hueRadians))
        }

        fun getInt(hue: Double, chroma: Double, lstar: Double, frame: Frame): Int {
            if (chroma < 1.0 || lstar.roundToInt() <= 0.0 || lstar.roundToInt() >= 100.0) {
                return CamUtils.intFromLstar(lstar)
            }
            var hue2 = 0.0
            if (hue >= 0.0) {
                hue2 = min(360.0, hue)
            }
            var high = chroma
            var mid = chroma
            var low = 0.0
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
                    mid = low + (high - low) / 2.0
                } else if (possibleAnswer != null) {
                    return possibleAnswer.viewed(frame)
                } else {
                    isFirstLoop = false
                    mid = low + (high - low) / 2.0
                }
            }
            return answer?.viewed(frame) ?: CamUtils.intFromLstar(lstar)
        }

        private fun findCamByJ(hue: Double, chroma: Double, lstar: Double): Cam? {
            var low = 0.0
            var high = 100.0
            var bestdL = 1000.0
            var bestdE = 1000.0
            var bestCam: Cam? = null
            while (abs(low - high) > LIGHTNESS_SEARCH_ENDPOINT) {
                val mid = low + (high - low) / 2.0
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
                if (bestdL == 0.0 && bestdE == 0.0) {
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