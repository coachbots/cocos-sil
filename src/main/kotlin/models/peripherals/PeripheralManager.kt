package models.peripherals

import interfaces.IRunsOnTick
import signals.Signal
import java.io.File
import java.io.InputStream

class PeripheralManager(private val gpioStream: File) : IRunsOnTick {
    val gpio = Gpio()

    override fun onTick(tickBegin: ULong, tickEnd: ULong) {
    }
}