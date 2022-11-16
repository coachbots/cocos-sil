package tests.assertions

import models.Coachbot

interface ICocosAssertion {
    fun assert(model: Coachbot): Boolean

    fun failMessage(): String
}