package tests.assertions

import java.lang.reflect.Field

open class CocosTestSuite(val name: String) {
    fun getTestsOfSuite(suite: CocosTestSuite): List<CocosTestBuilder> {
        return this.javaClass.declaredFields.filter { it.type == CocosTestBuilder::class.java }.map {
            it.get(suite) as CocosTestBuilder
        }
    }
}