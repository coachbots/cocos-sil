package models.signals.gpio

import models.peripherals.gpio.GpioDirection
import models.peripherals.gpio.GpioPullMode
import models.signals.ISignal
import models.signals.ISignalParser

/**
 * Represents an arbitrary GpioSignal attached to a concrete pin.
 *
 * @param pinBcm The BCM Pin Number
 *
 * @author Marko Vejnovic <contact@markovejnovic.com>
 */
open class GpioSignal(val pinBcm: Int) : ISignal {
    companion object: ISignalParser {
        /**
         * Returns this GpioSignal built from a string.
         *
         * @param signalBody The signal body.
         *
         * @return A dynamically dispatched signal cast into GpioSetSignal or GpioConfigureSignal
         */
        override fun fromString(signalBody: String): ISignal {
            val splitString = signalBody.split(",")
            val type: String = splitString[0]
            val pinBcm: Int = splitString[1].toInt()
            when(type) {
                "S" -> { return GpioSetSignal(pinBcm, splitString[2].toInt() == 1) }
                "C" -> {
                    return GpioConfigureSignal(
                        pinBcm,
                        if (splitString[2] == "I") { GpioDirection.IN } else { GpioDirection.OUT },
                        if (splitString[3] == "D") { GpioPullMode.DOWN } else { GpioPullMode.UP }
                    )
                }
            }
            throw IllegalArgumentException("$signalBody is not a GPIO signal of any known type.")
        }
    }
}