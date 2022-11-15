package models.signals.gpio

import models.peripherals.gpio.GpioDirection
import models.peripherals.gpio.GpioPullMode

/**
 * Represents a Gpio Signal configuring a pin to be an input or an output.
 *
 * @param pinBcm The BCM Pin number
 * @param direction The gpio direction (out or in)
 * @param pullMode The GPIO Pull mode (high or low)
 *
 * @author Marko Vejnovic <contact@markovejnovic.com>
 */
class GpioConfigureSignal(pinBcm: Int,
                          val direction: GpioDirection,
                          val pullMode: GpioPullMode) : GpioSignal(pinBcm)