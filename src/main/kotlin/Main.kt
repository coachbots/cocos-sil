import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import models.Coachbot
import models.peripherals.PeripheralManager
import mu.KotlinLogging

class CocosSil : CliktCommand() {
    val gpioStream by option(help="GPIO State Stream").file().required()
    val pwmStream by option(help="PWM State Stream").file().required()
    private val logger = KotlinLogging.logger {}

    companion object {
        const val MAX_TIME: Float = 10F
        const val TICK_SIZE: Float = 1E-3F
    }

    override fun run() {
        val peripheralManager = PeripheralManager(gpioStream)
        val coachbot = Coachbot(peripheralManager.gpio)

        var tick = 0UL
        while (tick < (MAX_TIME / TICK_SIZE).toULong()) {
            val currentTime = tick.toFloat() / TICK_SIZE
            val deltaTime = currentTime - (tick - 1UL).toFloat() / TICK_SIZE

            coachbot.onTick(currentTime, deltaTime)

            tick += 1UL
        }
    }
}

fun main(args: Array<String>) {
    CocosSil().main(args)
}