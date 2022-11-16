package tests.assertions

import models.Coachbot

class CocosTestRequirementsBuilder {
    private var assertionBuilders: ArrayList<ICocosAssertionRequirementsBuilder> = ArrayList()

    fun position(initializer: CocosPositionRequirementsBuilder.() -> Unit): CocosPositionRequirementsBuilder {
        val builder = CocosPositionRequirementsBuilder().apply(initializer)
        assertionBuilders.add(builder)
        return builder
    }

    fun ledColor(initializer: CocosLedRequirementsBuilder.() -> Unit): CocosLedRequirementsBuilder {
        val builder = CocosLedRequirementsBuilder().apply(initializer)
        assertionBuilders.add(builder)
        return builder
    }

    fun model(assertion: (bot: Coachbot) -> Boolean) {

    }

    fun build(): List<ICocosAssertion> {
        return assertionBuilders.map { it.build() }
    }
}