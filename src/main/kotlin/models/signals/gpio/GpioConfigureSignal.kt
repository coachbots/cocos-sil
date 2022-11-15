package models.signals.gpio

import models.peripherals.gpio.GpioDirection
import models.peripherals.gpio.GpioPullMode

class GpioConfigureSignal(pinBcm: Int,
                          val direction: GpioDirection,
                          val pullMode: GpioPullMode
) : GpioSignal(pinBcm)