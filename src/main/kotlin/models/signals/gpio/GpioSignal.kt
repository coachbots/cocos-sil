package models.signals.gpio

import models.peripherals.gpio.GpioDirection
import models.peripherals.gpio.GpioPullMode
import models.signals.ISignal
import models.signals.ISignalParser

open class GpioSignal(val pinBcm: Int) : ISignal {
    companion object: ISignalParser {
        override fun fromString(signalBody: String): ISignal {
            val splitString = signalBody.split(",")
            val type: String = splitString[0]
            val pinBcm: Int = splitString[1].toInt()
            when(type) {
                "S" -> GpioSetSignal(pinBcm, splitString[2].toInt() == 1)
                "C" -> GpioConfigureSignal(
                    pinBcm,
                    if (splitString[2] == "I") { GpioDirection.IN } else { GpioDirection.OUT },
                    if (splitString[3] == "D") { GpioPullMode.DOWN } else { GpioPullMode.UP }
                )
            }
            throw IllegalArgumentException("$signalBody is not a GPIO signal of any known type.")
        }
    }
}