package tests.runner

import sil.Simulator
import tests.assertions.CocosTestBuilder
import tests.assertions.CocosTestRunnable
import tests.assertions.CocosTestSuite
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.measureTimeMillis
import java.util.concurrent.TimeUnit
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

class TestRunner(private val cocosBinary: Path) {
    private val collector = TestCollector()
    private val errors: ArrayList<Triple<CocosTestSuite,
                                         CocosTestBuilder,
                                         CocosTestRunnable.CocosAssertionFailure>> = ArrayList()

    fun runTests(): Boolean {
        var cummulativeSuccess = true
        val gpioPath = Path("/", "tmp", "cocos-sil-gpio.out")

        for (testSuiteClass in collector.testSuites) {
            val testSuite = testSuiteClass.getConstructor().newInstance()
            testSuitePrint("Running \"${testSuite.name}\"")

            for (test in testSuite.getTestsOfSuite(testSuite)) {
                // Run cocos for as many seconds as we asked
                val cocosProc = ProcessBuilder(cocosBinary.absolutePathString(),
                                               "-u=--",
                                               "--gpio-file=${gpioPath.absolutePathString()}").start()
                cocosProc.outputStream.write(test.targetScript!!.encodeToByteArray())
                cocosProc.waitFor((1000F * (test.endingAt + COCOS_INITIALIZATION_TIME)).toLong(), TimeUnit.MILLISECONDS)
                cocosProc.destroy()

                val gpioStream = File(gpioPath.toUri())

                // Run the actual SIL simulator.
                val testRunnable = test.build()
                val simulator = Simulator(test.coachbotInitializer, gpioStream, test.endingAt) {
                        timeBegin, timeEnd, model -> testRunnable.onTick(timeBegin, timeEnd, model)
                }

                var successFlag = false
                val executionTime = measureTimeMillis {
                    try {
                        simulator.runSimulation()
                        successFlag = true
                    } catch (assertionError: CocosTestRunnable.CocosAssertionFailure) {
                        errors.add(Triple(testSuite, test, assertionError))
                    }
                }

                val sEmoji = if (successFlag) { "PASS️" } else { "FAIL" }
                testCasePrint(test.name.padEnd(40, ' ') +
                              "${executionTime}ms".padEnd(10) +
                              sEmoji)
                cummulativeSuccess = successFlag && cummulativeSuccess
            }
        }

        if (errors.size != 0) {
            println("Errors:")
            dumpErrors()
        }

        return cummulativeSuccess
    }

    private fun dumpErrors() {
        for ((suite, test, error) in errors) {
            println("  | ${suite.name}::${test.name}")
            println("  |-> Expected :: ${error.assertion.failMessage()}")
            println("  |-> Actual   :: ${error.modelState}")
        }
    }

    companion object {
        private fun testSuitePrint(message: Any?) {
            println(message)
        }

        private fun testCasePrint(message: Any?) {
            println("  |-> $message")
        }

        const val COCOS_INITIALIZATION_TIME: Float = 2F;
    }
}