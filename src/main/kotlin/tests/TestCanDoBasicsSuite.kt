package tests

import dev.romainguy.kotlin.math.Float2
import models.Coachbot
import models.Color
import tests.assertions.CocosTestBuilder
import tests.assertions.CocosTestSuite

class TestCanDoBasicsSuite : CocosTestSuite(name = "Basics") {
    @JvmField
    val testMovesForward = CocosTestBuilder.test(
        "Test Coachbot Moves Forward",
        "This test tests whether the coachbot can move forward from an initial position.",
        2F
    ) {
        withScript("""
            def usr(bot):
                while True:
                    bot.set_vel(100, 100)
                    bot.delay()
        """.trimIndent())
        withCoachbotInitializer { Coachbot(it, posCenter = Float2(0F, 0F)) }
        assert(1.0F) {
            position {
                distanceFromOrigin(1.0F, 1E-2F)
            }
        }
    }

    @JvmField
    val testLedCanBeChanged = CocosTestBuilder.test(
        name = "Test LED Color Change",
        description = "Tests whether the LED can be changed via user-code.",
        endingAt = 1F
    ) {
        withScript("""
            def usr(bot):
                while True:
                    bot.set_led(100, 0, 0)
                    bot.delay(1000)
                    bot.set_led(0, 100, 0)
                    bot.delay(1000)
                    bot.set_led(0, 0, 100)
        """.trimIndent())

        assert(at = 1.01F) { ledColor { appears(Color.RED) } }
        assert(at = 2.01F) { ledColor { appears(Color.GREEN) } }
        assert(at = 3.01F) { ledColor { appears(Color.BLUE) } }
    }
}