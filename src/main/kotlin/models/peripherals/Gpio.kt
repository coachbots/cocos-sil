package models.peripherals

import signals.GpioConfigureSignal
import signals.GpioSetSignal
import signals.GpioSignal

enum class GpioDirection { OUT, IN }
enum class GpioPullMode { UP, DOWN }

data class GpioState(var isHigh: Boolean, val direction: GpioDirection, val pullMode: GpioPullMode) {
    companion object {
        fun empty(): GpioState {
            return GpioState(false, GpioDirection.OUT, GpioPullMode.UP);
        }
    }
}

class Gpio {
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