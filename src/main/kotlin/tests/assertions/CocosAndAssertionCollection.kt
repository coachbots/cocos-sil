package tests.assertions

import models.Coachbot

class CocosAndAssertionCollection(private val asserts: List<ICocosAssertion>): ICocosAssertion {
    override fun assert(model: Coachbot): Boolean {
        return asserts.map { it.assert(model) }.reduce { acc, b -> acc && b }
    }

    override fun failMessage(): String {
        return asserts.map { it.failMessage() }.reduce { acc, s -> "$acc && ($s)" }
    }
}