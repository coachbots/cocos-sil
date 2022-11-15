package models.motor

import models.peripherals.gpio.GpioPeripheral

open class BaseMotorModel(private val gpio: GpioPeripheral) {
    var angle: Float = 0F

    fun isBlocked(): Boolean {
        return !gpio[MotorLeftModel.PIN_MAP["PIN_STDBY"]!!].isHigh
                || (gpio[MotorLeftModel.PIN_MAP["PIN_A"]!!].isHigh && gpio[MotorLeftModel.PIN_MAP["PIN_B"]!!].isHigh)
    }

    /**
     * Runs the model on a tick, returning the change in angle that the motor experienced.
     */
    fun onTick(currentTime: Float, deltaTime: Float): Float {
        var deltaAngle = 0F
        if (!isBlocked()) {
            // TODO: Check if these directions are correct
            // If the left pin is high, drive clockwise
            if (gpio[MotorLeftModel.PIN_MAP["PIN_A"]!!].isHigh) {
                deltaAngle = MotorLeftModel.MAX_ANG_VEL * deltaTime
            }

            // Otherwise we turn the wheel to the right
            if (gpio[MotorLeftModel.PIN_MAP["PIN_B"]!!].isHigh) {
                deltaAngle = -(MotorLeftModel.MAX_ANG_VEL * deltaTime)
            }
        }
        angle += deltaAngle

        return deltaAngle
    }
}