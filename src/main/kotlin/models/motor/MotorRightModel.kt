package models.motor

import models.peripherals.gpio.GpioPeripheral

class MotorRightModel(gpio: GpioPeripheral) : BaseMotorModel(gpio) {
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