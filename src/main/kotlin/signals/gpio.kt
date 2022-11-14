package signals

import models.peripherals.GpioDirection
import models.peripherals.GpioPullMode

open class GpioSignal(val pinBcm: Int)

class GpioConfigureSignal(pinBcm: Int,
                          val direction: GpioDirection,
                          val pullMode: GpioPullMode
) : GpioSignal(pinBcm)

class GpioSetSignal(pinBcm: Int, val value: Boolean) : GpioSignal(pinBcm)

fun parseGpioSignalString(string: String): GpioSignal {
    val splitString = string.split(",")
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
    throw IllegalArgumentException("$string is not a GPIO signal of any known type.")
}