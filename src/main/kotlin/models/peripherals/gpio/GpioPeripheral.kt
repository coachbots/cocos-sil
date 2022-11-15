package models.peripherals.gpio

import models.signals.gpio.GpioConfigureSignal
import models.signals.gpio.GpioSetSignal
import models.signals.gpio.GpioSignal

class GpioPeripheral {
    private val gpioMap = Array(NUM_PINS) { GpioState.empty() }

    operator fun get(pin: Int): GpioState {
        return gpioMap[pin]
    }

    fun onSignal(signal: GpioSignal) {
        when(signal) {
            is GpioSetSignal -> {
                gpioMap[signal.pinBcm].isHigh = signal.value
            }
            is GpioConfigureSignal -> {
                gpioMap[signal.pinBcm] = GpioState(false, signal.direction, signal.pullMode)
            }
        }
    }

    companion object {
        const val NUM_PINS = 27;
    }
}