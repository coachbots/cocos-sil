package models.signals.gpio

/**
 * Represents a signal that sets a GPIO pin high or low.
 *
 * @param pinBcm The BCM pin number
 * @param value True if the pin is set high
 *
 * @author Marko Vejnovic <contact@markovejnovic.com>
 */
class GpioSetSignal(pinBcm: Int, val value: Boolean) : GpioSignal(pinBcm)