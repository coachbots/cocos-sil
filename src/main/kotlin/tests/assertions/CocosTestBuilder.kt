package tests.assertions

import models.Coachbot
import models.peripherals.PeripheralManager

class CocosTestBuilder(val name: String, val description: String, val endingAt: Float) {
    private val testRequirements: ArrayList<Pair<Float, CocosTestRequirementsBuilder>> = ArrayList()
    var targetScript: String? = null
    var coachbotInitializer: (PeripheralManager) -> Coachbot = { peripheralManager -> Coachbot(peripheralManager) }

    companion object {
        fun test(name: String,
                 description: String = "",
                 endingAt: Float,
                 initializer: CocosTestBuilder.() -> Unit): CocosTestBuilder {
            return CocosTestBuilder(name, description, endingAt).apply(initializer)
        }
    }

    fun build(): CocosTestRunnable {
        validate()
        return CocosTestRunnable(testRequirements.associate { Pair(it.first, it.second.build()) })
    }

    /**
     * Feeds a user script into the test.
     */
    fun withScript(script: String) {
        targetScript = script
    }

    /**
     * Initializes the coachbot initial parameters.
     */
    fun withCoachbotInitializer(initializer: (PeripheralManager) -> Coachbot) {
        coachbotInitializer = initializer
    }

    /**
     * Asserts all conditions within this block are true. A single failing one will return a failed test.
     *
     * @param at The time (in seconds) at which the assertion is to be evaluated.
     */
    fun assert(at: Float, initializer: CocosTestRequirementsBuilder.() -> Unit): CocosTestRequirementsBuilder {
        val requirementsBuilder = CocosTestRequirementsBuilder().apply(initializer)
        testRequirements.add(Pair(at, requirementsBuilder))
        return requirementsBuilder
    }

    /**
     * Validates the builder ensuring that all the parameters are correct.
     *
     * @see build For more information on when this is run.
     *
     * @throws IllegalArgumentException Upon an invalid builder.
     */
    private fun validate() {
        if (targetScript == null) {
            throw IllegalArgumentException("The test is missing a withScript call.")
        }
    }
}