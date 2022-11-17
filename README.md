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

## Releases

This repository has integrated CI/CD so that releases are automatically built. Tags marked by the glob `v*.*.*` (see 
Versioning) will automatically get built, producing a [fat-jar](https://imagej.net/develop/uber-jars) and releasing it
under github releases. If you only want to use this repository, simply fetch the release `jar` you are interested in and
run it on a JVM.

## Versioning

This repository uses [semver](https://semver.org/):
```text
<MAJOR>.<MINOR>.<PATCH>
```
`<MAJOR>` bumps indicate an API-breaking change, meaning that `v<n+1>.*.*` releases are not compatible with `v<n>.*.*`
releases. `<MINOR>` bumps indicate backward-supporting feature additions and `<PATCH>` bumps indicate bugfixes which are
necessarily backwards compatible.

## Developing

### Getting Started

Developing for this repository is relatively easy. To get you started:
```shell
git clone git@github.com:coachbots/cocos-sil.git
cd cocos-sil
./gradlew build --debug
```

This will clone the repository and build it with `gradle`.

### Developing Features

If you wish to develop features, please create a new branch which will be attached to an issue. If, for example, there
is an issue `1`, consider labelling your branch something like:

```shell
git checkout -b user/<myusername>/1-fix-broken-sil
```

When you wish to integrate this into `master` (which is the stable release branch), target `master`, increasing the
version number, squashing and merging. Please ensure your commit message contains the issue number so that it is easier
to reference the relevant issue. Upon successful merge, do not forget to apply a new tag:

```shell
git checkout master
git pull
git tag -a v1.1.3 -m "Release Message"
git push origin v1.1.3
```

This will automatically trigger the release CI/CD action which will create a release for you.

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