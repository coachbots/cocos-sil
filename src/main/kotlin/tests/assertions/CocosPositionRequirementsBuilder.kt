package tests.assertions

import dev.romainguy.kotlin.math.Float2
import models.Coachbot
import kotlin.math.pow

class CocosPositionRequirementsBuilder: ICocosAssertionRequirementsBuilder {
    private val requirements: ArrayList<ICocosAssertion> = ArrayList()

    private class CocosDistanceAssertion(private val from: Float2,
                                         private val target: Float,
                                         private val tolerance: Float = Float.MIN_VALUE): ICocosAssertion {
        override fun assert(model: Coachbot): Boolean {
            val distVec = model.posCenter - from
            val distance = StrictMath.sqrt(distVec.x.toDouble().pow(2.0) + distVec.y.toDouble().pow(2.0))
            return target - tolerance <= distance && distance <= target + tolerance
        }

        override fun failMessage(): String = "Distance from $from is ($target +- $tolerance)m"
    }

    /**
     * Ensures that the coachbot is "value" meters away from "from".
     *
     * @param from The distance to calculate from
     * @param value The target distance
     * @param tolerance The acceptable tolerance for testing
     */
    fun distance(from: Float2, value: Float, tolerance: Float = Float.MIN_VALUE) {
        requirements.add(CocosDistanceAssertion(from, value, tolerance))
    }

    private class CocosPositionAssertion(
        private val at: Float2,
        private val tolerance: Float2 = Float2(Float.MIN_VALUE, Float.MIN_VALUE)
    ): ICocosAssertion {
        override fun assert(model: Coachbot): Boolean {
            return at.x - tolerance.x <= model.posCenter.x && model.posCenter.x <= at.x + tolerance.x
                    && at.y - tolerance.y <= model.posCenter.y && model.posCenter.y <= at.y + tolerance.y
        }

        override fun failMessage(): String = "Position is $at +- $tolerance"
    }

    /**
     * Ensures that the coachbot is at a position "vec".
     *
     * @param vec The target position
     * @param tolerance The maximum x,y errors for which the value is acceptable.
     */
    fun isAt(vec: Float2, tolerance: Float2 = Float2(Float.MIN_VALUE, Float.MIN_VALUE)) {
        requirements.add(CocosPositionAssertion(vec, tolerance))
    }

    /**
     * Asserts that a coachbot is "value" meters away from the origin.
     *
     * @param value The target distance
     * @param tolerance The acceptable tolerance.
     */
    fun distanceFromOrigin(value: Float, tolerance: Float = Float.MIN_VALUE) {
        return distance(Float2(0F, 0F), value, tolerance)
    }

    override fun build(): ICocosAssertion {
        return CocosAndAssertionCollection(requirements)
    }
}