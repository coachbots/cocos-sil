package tests.runner

import org.reflections.Reflections
import tests.assertions.CocosTestSuite

class TestCollector {
    val testSuites: Set<Class<out CocosTestSuite>>
        get() = Reflections("tests").getSubTypesOf(CocosTestSuite::class.java)
}