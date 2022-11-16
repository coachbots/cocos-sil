package tests.assertions

import models.Coachbot
import models.Color

/**
 * Assertions related to ensuring LED state.
 */
class CocosLedRequirementsBuilder: ICocosAssertionRequirementsBuilder {
    private val requirements: ArrayList<ICocosAssertion> = ArrayList()

    /**
     * Performs a very fuzzy check whether the LED appears to be of a specific color to humans.
     */
    fun appears(color: Color) {
        requirements.add(CocosFuzzyLedAssertion(color))
    }

    private class CocosFuzzyLedAssertion(private val targetColor: Color): ICocosAssertion {
        override fun assert(model: Coachbot): Boolean {
            return model.ledColor.appearsSimilarTo(targetColor)
        }

        override fun failMessage(): String = "LED Looks like $targetColor"
    }

    /**
     * Performs a check ensuring that the color of the LED is exactly the given value.
     */
    fun isExact(color: Color) {
        requirements.add(CocosExactLedAssertion(color))
    }

    private class CocosExactLedAssertion(private val targetColor: Color): ICocosAssertion {
        override fun assert(model: Coachbot): Boolean {
            return model.ledColor == targetColor
        }

        override fun failMessage(): String = "LED is exactly $targetColor"
    }

    override fun build(): ICocosAssertion {
        return CocosAndAssertionCollection(requirements)
    }
}