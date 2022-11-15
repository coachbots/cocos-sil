package models.signals

import models.signals.gpio.GpioSignal
import kotlin.reflect.KClass

enum class SignalType(val value: Int, val signalClass: KClass<*>?) {
    GPIO(1, GpioSignal::class);

    companion object {
        fun fromInt(value: Int) = SignalType.values().first { it.value == value }
    }
}