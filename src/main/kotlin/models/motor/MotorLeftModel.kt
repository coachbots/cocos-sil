package models.motor

import models.peripherals.gpio.GpioPeripheral

class MotorLeftModel(gpio: GpioPeripheral) :  BaseMotorModel(gpio) {
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