package tests.runner

import models.Coachbot
import sil.Simulator
import tests.assertions.CocosTestBuilder
import tests.assertions.CocosTestRunnable
import tests.assertions.CocosTestSuite
import java.io.File
import kotlin.system.measureTimeMillis

class TestRunner {
    private val collector = TestCollector()
    private val errors: ArrayList<Triple<CocosTestSuite,
                                         CocosTestBuilder,
                                         CocosTestRunnable.CocosAssertionFailure>> = ArrayList()

    fun runTests(gpioStream: File): Boolean {
        var cummulativeSuccess = true
        for (testSuiteClass in collector.testSuites) {
            val testSuite = testSuiteClass.getConstructor().newInstance()
            testSuitePrint("Running \"${testSuite.name}\"")

            for (test in testSuite.getTestsOfSuite(testSuite)) {
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
    }
}