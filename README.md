# cocos-sil

This repository holds **cocos-sil**, the SIL runner for `cocos`, the Coachbot firmware.

### SIL Testing

SIL (software-in-loop) testing is a testing method that models and simulates the behavior of a system, usually via an
integrating method. Test requirements are injected into the system running assertions on target systems. You can imagine
the system to operate along the lines of the following pseudo-code:

```python
def sil():
    # Initialize the simulated system
    target_system = TargetSystem(...)
    current_tick = 0

    loop:
        target_system.on_tick()  # Simulate the behavior of the system for a single tick
        
        run_tests(tick)  # Run tests that correspond to the tick.
        
        current_tick += 1
        
def run_tests(tick):
    if tick == 32:
        run_my_test_on_tick_32()
```

## Defense

I deem it necessary to have a decent, stable and robust system. SIL testing aids in ensuring that firmware never
regresses and reduces the need for manual testing to near-zero.

### Why Kotlin?

As you will have noticed, this repository is written in Kotlin. Fundamentally the tradeoff for deciding which language
to use was one of balancing the following three variables:

* Department Language Familiarity - *It is quite important that someone can maintain this software after I leave NU.*
* Performance - *It is somewhat important to have performance that can run long-running tests with small tick sizes in
a time scale that is not overly long*.
* Development Time - *This project is an additional project to `cocos` and therefore there is a goal of not investing
too much time into this project.*

Kotlin scores quite well in the two last points, but almost no one at the CRB is familiar with it. Sadly, the CRB seems
to be familiar with *Python* and *C++* only, but neither of those languages fit the latter two requirements.
Consequently, *Kotlin* (being very similar to Java) was chosen and the test framework was designed to be as unintrusive
as possible.

## Tests

With the goal of making tests as simple to write as possible, I've written an easy-to-use testing framework. The
`main.kotlin.tests` package holds all the tests that are registered.

### Adding Tests

In order to add tests, simply add a new `.kt` file into `main.kotlin.tests` with a singular class encompassing a test
suite, like so:

```kotlin
package tests

import models.Color
import assertions.CocosTestBuilder

class TestCanDoBasicsSuite {
    val testLedCanBeChanged = CocosTestBuilder.test(
        name = "Test LED Color Change",
        description = "Tests whether the LED can be changed via user-code.",
        endingAt = 3.5F
    ) {
        // Run the simulation with this user script.
        withScript("""
            def usr(bot):
                while True:
                    bot.set_led(100, 0, 0)
                    bot.delay(1000)
                    bot.set_led(0, 100, 0)
                    bot.delay(1000)
                    bot.set_led(0, 0, 100)
        """.trimIndent())

        // Assert that at 1.01s the LED color will look as if it is red.
        assert(at = 1.01F) { ledColor { appears(Color.RED) } }
        
        // Also assert at the 2.01s the LED color will look green.
        assert(at = 2.01F) { ledColor { appears(Color.GREEN) } }
        
        // And, also assert that at 3.01s the LED color will look blue.
        assert(at = 3.01F) { ledColor { appears(Color.BLUE) } }
    }
}
```

As you can see, although you may not be familiar with *Kotlin*, the testing framework is extremely easy to use as all
you have to do is simply declare your test via the API and the runner will do all the hard work!