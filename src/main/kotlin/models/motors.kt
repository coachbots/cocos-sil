package models

import models.peripherals.Gpio

interface IMotorModel {}

open class BaseMotor(private val gpio: Gpio) {
    var angle: Float = 0F

    fun isBlocked(): Boolean {
        return !gpio[MotorLeft.PIN_MAP["PIN_STDBY"]!!].isHigh
                || (gpio[MotorLeft.PIN_MAP["PIN_A"]!!].isHigh && gpio[MotorLeft.PIN_MAP["PIN_B"]!!].isHigh)
    }

    /**
     * Runs the model on a tick, returning the change in angle that the motor experienced.
     */
    fun onTick(currentTime: Float, deltaTime: Float): Float {
        var deltaAngle = 0F
        if (!isBlocked()) {
            // TODO: Check if these directions are correct
            // If the left pin is high, drive clockwise
            if (gpio[MotorLeft.PIN_MAP["PIN_A"]!!].isHigh) {
                deltaAngle = MotorLeft.MAX_ANG_VEL * deltaTime
            }

            // Otherwise we turn the wheel to the right
            if (gpio[MotorLeft.PIN_MAP["PIN_B"]!!].isHigh) {
                deltaAngle = -(MotorLeft.MAX_ANG_VEL * deltaTime)
            }
        }
        angle += deltaAngle

        return deltaAngle
    }
}

class MotorLeft(gpio: Gpio) : IMotorModel, BaseMotor(gpio) {
    companion object {
        /**
         * Defines the maximum angular velocity in rad/s (ie. the velocity at DC 100%)
         */
        const val MAX_ANG_VEL = 1
        val PIN_MAP = mapOf(
            "PIN_A" to 27,
            "PIN_B" to 26,
            "PIN_PWM" to 25,
            "PIN_STDBY" to 23
        )
    }
}

class MotorRight(gpio: Gpio) : IMotorModel, BaseMotor(gpio) {
    companion object {
        const val MAX_ANG_VEL = 1
        val PIN_MAP = mapOf(
            "PIN_A" to 27,
            "PIN_B" to 26,
            "PIN_PWM" to 25,
            "PIN_STDBY" to 23
        )
    }
}