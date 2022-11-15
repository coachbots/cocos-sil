package models.signals.gpio

class GpioSetSignal(pinBcm: Int, val value: Boolean) : GpioSignal(pinBcm)