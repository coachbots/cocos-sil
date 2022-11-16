package models

import dev.romainguy.kotlin.math.Float3
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Simple class for modelling colors.
 */
class Color(val r: Float, val g: Float, val b: Float, val a: Float = 1.0F) {
    /**
     * Checks whether this color appears similar to another class. This is done by converting the colors into HSV space
     * and comparing the values in order to ensure that they fit within tolerances. The implementation is pretty
     * lenient.
     */
    fun appearsSimilarTo(other: Color): Boolean {
        val selfHsv = hsv
        val otherHsv = other.hsv

        if (abs(selfHsv.third - other.hsv.third) > 40E-3) { return false }
        if (abs(selfHsv.second - other.hsv.second) > 40E-3) { return false }
        if (abs(selfHsv.first - otherHsv.first) > 20) { return false }

        return true
    }

    val rgb: Triple<Float, Float, Float>
        get() = Triple(r, g, b)

    val hsv: Triple<Float, Float, Float>
        get() {
            val cMax = max(r, max(g, b))
            val cMin = min(r, min(g, b))
            val cDelta = cMax - cMin

            val hue: Float = when(cDelta) {
                0F -> 0F
                else ->
                    when(cMax) {
                        r -> 60F * (((g - b) / cDelta) % 6.0F)
                        g -> 60F * (((b - r) / cDelta) + 2.0F)
                        b -> 60F * (((r - g) / cDelta) + 4.0F)
                        else -> throw IllegalArgumentException("Invalid color value")
                    }
                }

            val sat: Float = when(cMax) {
                0F -> 0F
                else -> cDelta / cMax
            }

            return Triple(hue, sat, cMax)
        }

    override fun toString(): String = "Color($r, $g, $b)"

    companion object {
        val RED = Color(1F, 0F, 0F)
        val GREEN = Color(0F, 1F, 0F)
        val BLUE = Color(0F, 0F, 1F)

        fun fromFloat3(vec: Float3): Color {
            return Color(vec[0], vec[1], vec[2])
        }
    }
}