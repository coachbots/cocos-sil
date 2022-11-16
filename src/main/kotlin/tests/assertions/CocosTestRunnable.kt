package tests.assertions

import models.Coachbot

class CocosTestRunnable(private val assertionMap: Map<Float, List<ICocosAssertion>>) {
    fun onTick(tickBegin: Float, tickEnd: Float, model: Coachbot) {
        for ((timestamp, assertions) in assertionMap) {
            if (timestamp in tickBegin..tickEnd) {
                for (assertion in assertions) {
                    if (!assertion.assert(model)) {
                        throw CocosAssertionFailure(tickBegin, model, assertion)
                    }
                }
            }
        }
    }

    class CocosAssertionFailure(val timestamp: Float,
                                val modelState: Coachbot,
                                val assertion: ICocosAssertion): Exception()
}