package models.peripherals

import interfaces.IRunsOnTick
import models.peripherals.gpio.GpioPeripheral
import models.signals.ISignal
import models.signals.gpio.GpioSignal
import java.io.File

class PeripheralManager(private val gpioStream: File) : IRunsOnTick {
    val gpio = GpioPeripheral()

    override fun onTick(tickBegin: ULong, tickEnd: ULong) {
    }

    fun onSignal(timestamp: Float, signal: ISignal) {
        when(signal) {
            is GpioSignal -> { gpio.onSignal(signal) }
        }
    }
}