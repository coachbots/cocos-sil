package sil

import models.Coachbot
import models.peripherals.PeripheralManager
import java.io.File

class Simulator(coachbotInitializer: (PeripheralManager) -> Coachbot,
                private val gpioStream: File,
                private val stopTime: Float,
                private val tickHook: (timeBegin: Float, timeEnd: Float, model: Coachbot) -> Unit
) {
    companion object {
        const val TICK_SIZE: Float = 1E-3F
    }

    private val peripheralManager = PeripheralManager(gpioStream)
    private val coachbot = coachbotInitializer(peripheralManager)

    fun runSimulation() {
        gpioStream.bufferedReader().use {
            val signalParser = SignalStreamParser(it, peripheralManager)

            var tick = 0UL
            while (tick < (stopTime / TICK_SIZE).toULong()) {
                val currentTime = tick.toFloat() * TICK_SIZE
                val nextTime = (tick + 1UL).toFloat() * TICK_SIZE
                val deltaTime = nextTime - currentTime

                signalParser.onTick(currentTime, nextTime)
                coachbot.onTick(currentTime, deltaTime)

                tickHook(currentTime, nextTime, coachbot)

                tick += 1UL
            }
        }
    }
}