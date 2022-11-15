import assertions.TestSuite
import assertions.cocosTest
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.file
import dev.romainguy.kotlin.math.Float2
import models.Coachbot
import models.Color
import models.peripherals.PeripheralManager
import models.signals.SignalMessage
import mu.KotlinLogging
import sun.misc.Signal
import java.io.BufferedReader
import java.io.File

class SignalStreamParser(private val reader: BufferedReader, private val peripheralManager: PeripheralManager) {
    private var messageBuffer: ArrayList<SignalMessage> = ArrayList()

    fun onTick(timeBegin: Float, timeEnd: Float) {
        val signalsToDispatch = getSignalMessagesInSlice(timeBegin, timeEnd)
        for (signalMsg in signalsToDispatch) {
            peripheralManager.onSignal(timeBegin, signalMsg.signal)
        }
    }

    private fun getSignalMessagesInSlice(timeBegin: Float, timeEnd: Float): Array<SignalMessage> {
        // Attempt first to copy over any elements that may have survived since the last time-step.
        val remnantsFromLastCall = messageBuffer.filter { it.timestamp >= timeBegin }
        if (remnantsFromLastCall.size != messageBuffer.size) {
            // TODO: Wrong exception lmao
            throw IllegalArgumentException("Not all Signals were handled from the messageBuffer of the previous " +
                                           "time step.")
        }
        messageBuffer = ArrayList()

        val toReturn: ArrayList<SignalMessage> = ArrayList(remnantsFromLastCall)

        // Keep reading new signals until we reach a point where the signal time is simply too high.
        var currentLine = reader.readLine()
        if (currentLine != null) {
            var currentMessage = SignalMessage.fromString(currentLine)
            while (currentMessage.timestamp < timeEnd) {
                toReturn.add(currentMessage)
                currentLine = reader.readLine()
                if (currentLine == null) { break }

                currentMessage = SignalMessage.fromString(currentLine)
            }
            // Now, note that we have one dangling signal -- push him to the messageBuffer
            messageBuffer.add(currentMessage)
        }

        return toReturn.toTypedArray()
    }
}

class CocosSil : CliktCommand() {
    val gpioStream by option(help="GPIO State Stream").file().required()
    private val logger = KotlinLogging.logger {}

    companion object {
        const val MAX_TIME: Float = 10F
        const val TICK_SIZE: Float = 1E-3F
    }

    override fun run() {
        val peripheralManager = PeripheralManager(gpioStream)
        val coachbot = Coachbot(peripheralManager.gpio)
        gpioStream.bufferedReader().use {
            val signalParser = SignalStreamParser(it, peripheralManager)

            var tick = 0UL
            while (tick < (MAX_TIME / TICK_SIZE).toULong()) {
                val currentTime = tick.toFloat() * TICK_SIZE
                val nextTime = (tick + 1UL).toFloat() * TICK_SIZE
                val deltaTime = nextTime - currentTime

                signalParser.onTick(currentTime, nextTime)
                coachbot.onTick(currentTime, deltaTime)

                tick += 1UL
            }
        }
    }
}
fun main(args: Array<String>) {
    CocosSil().main(args)
}