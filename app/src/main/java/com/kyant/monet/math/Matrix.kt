package com.kyant.monet.math

typealias Matrix = Array<DoubleArray>
typealias Matrix3 = Array<DoubleArray>
typealias Matrix3x1 = DoubleArray

fun matrix3Of(
    m11: Double,
    m12: Double,
    m13: Double,
    m21: Double,
    m22: Double,
    m23: Double,
    m31: Double,
    m32: Double,
    m33: Double
): Matrix3 = arrayOf(
    doubleArrayOf(m11, m12, m13),
    doubleArrayOf(m21, m22, m23),
    doubleArrayOf(m31, m32, m33)
)

fun zeroMatrix3Of(): Matrix3 = matrix3Of(
    .0, .0, .0,
    .0, .0, .0,
    .0, .0, .0
)

fun matrix3x1Of(
    m11: Double,
    m21: Double,
    m31: Double,
): Matrix3x1 = doubleArrayOf(m11, m21, m31)

fun zeroMatrix3x1Of(): Matrix3x1 = matrix3x1Of(.0, .0, .0)

operator fun Matrix3.times(other: Matrix3x1): Matrix3x1 {
    return doubleArrayOf(
        other[0] * this[0][0] + other[1] * this[0][1] + other[2] * this[0][2],
        other[0] * this[1][0] + other[1] * this[1][1] + other[2] * this[1][2],
        other[0] * this[2][0] + other[1] * this[2][1] + other[2] * this[2][2]
    )
}

fun Matrix.inverse(): Matrix {
    val len = this.size
    require(this.all { it.size == len }) { "Not a square matrix" }
    val aug = Array(len) { DoubleArray(2 * len) }
    for (i in 0 until len) {
        for (j in 0 until len) aug[i][j] = this[i][j]
        aug[i][i + len] = 1.0
    }
    aug.toReducedRowEchelonForm()
    val inv = Array(len) { DoubleArray(len) }
    for (i in 0 until len) {
        for (j in len until 2 * len) inv[i][j - len] = aug[i][j]
    }
    return inv
}

fun Matrix.toReducedRowEchelonForm() {
    var lead = 0
    val rowCount = this.size
    val colCount = this[0].size
    for (r in 0 until rowCount) {
        if (colCount <= lead) return
        var i = r

        while (this[i][lead] == 0.0) {
            i++
            if (rowCount == i) {
                i = r
                lead++
                if (colCount == lead) return
            }
        }

        val temp = this[i]
        this[i] = this[r]
        this[r] = temp

        if (this[r][lead] != 0.0) {
            val div = this[r][lead]
            for (j in 0 until colCount) this[r][j] /= div
        }

        for (k in 0 until rowCount) {
            if (k != r) {
                val mult = this[k][lead]
                for (j in 0 until colCount) this[k][j] -= this[r][j] * mult
            }
        }

        lead++
    }
}

operator fun DoubleArray.component6(): Double = get(5)