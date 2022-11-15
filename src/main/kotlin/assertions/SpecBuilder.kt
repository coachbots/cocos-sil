package assertions

import dev.romainguy.kotlin.math.Float2
import models.Coachbot
import models.Color

interface ICocosAssertionRequirementsBuilder {
    fun build(): ICocosAssertion
}

interface ICocosAssertion {
    fun assert(model: Coachbot): Boolean
}

class CocosPositionRequirementsBuilder: ICocosAssertionRequirementsBuilder {
    private var posBounds = Pair(Float2(0F, 0F), Float2(0F, 0F))

    fun distance(from: Float2, isApprox: Float) {
    }

    fun isAt(vec: Float2) {
        posBounds = Pair(vec, vec)
    }

    fun distanceFromOrigin(isApprox: Float) {
        return distance(Float2(0F, 0F), isApprox)
    }

    override fun build(): ICocosAssertion {
        return CocosPositionAssertion(posBounds)
    }

    class CocosPositionAssertion(val posBounds: Pair<Float2, Float2>) : ICocosAssertion {
        override fun assert(model: Coachbot): Boolean {
            return (posBounds.first.x < model.posCenter.x && model.posCenter.x < posBounds.second.x
                && posBounds.first.y < model.posCenter.y && model.posCenter.y < posBounds.second.y)
        }
    }
}

class CocosLedRequirementsBuilder: ICocosAssertionRequirementsBuilder {
    fun appears(color: Color) {

    }

    override fun build(): ICocosAssertion {
        TODO("Not yet implemented")
    }
}

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

    fun build(): List<ICocosAssertion> {
        return assertionBuilders.map { it.build() }
    }
}

class CocosAssertion {

}

class CocosTestRunner(private val assertionMap: List<Pair<Float, List<ICocosAssertion>>>) {
    fun onTick(timestamp: Float, model: Coachbot) {
        for (value in assertionMap) {
            val localTimestamp = value.first
            if (timestamp != localTimestamp) {
                continue
            }
            val tests = value.second

            for (test in tests) {
                test.assert(model)
            }
        }
    }
}

class CocosSpecBuilder(val name: String, val description: String) {
    private val testRequirements: ArrayList<Pair<Float, CocosTestRequirementsBuilder>> = ArrayList()
    private var targetScript: String = ""

    fun build(): CocosTestRunner {
        return CocosTestRunner(testRequirements.map { Pair(it.first, it.second.build()) })
    }

    fun withScript(script: String) {
        targetScript = script
    }

    fun ensure(at: Float, initializer: CocosTestRequirementsBuilder.() -> Unit): CocosTestRequirementsBuilder {
        val requirementsBuilder = CocosTestRequirementsBuilder().apply(initializer)
        testRequirements.add(Pair(at, requirementsBuilder))
        return requirementsBuilder
    }
}

fun cocosTest(name: String,
              description: String = "",
              endingAt: Float,
              initializer: CocosSpecBuilder.() -> Unit): CocosSpecBuilder {
    return CocosSpecBuilder(name, description).apply(initializer)
}

class TestSuite(vararg tests: CocosSpecBuilder) {
    private val testRunners = tests.map { it.build() }
    fun onTick(timestamp: Float, coachbot: Coachbot) {
        for (testRunner in testRunners) {
            testRunner.onTick(timestamp, coachbot)
        }
    }
}

fun myF() {
    cocosTest("Coachbot with only vel command moves from origin.", endingAt = 5.0F) {
        withScript("""
            def usr(bot):
                while True:
                    bot.set_vel(100, 100)
                    bot.set_led(100, 0, 0)
                    bot.delay()
        """.trimIndent())
        ensure(at = 3.0F) {
            position {
                distanceFromOrigin(1F)
            }
            ledColor {
                appears(Color.RED)
            }
        }
        ensure(at = 4.0F) {
            ledColor { appears(Color.RED) }
        }
    }.build()
}