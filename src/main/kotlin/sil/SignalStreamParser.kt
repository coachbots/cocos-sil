package sil

import models.peripherals.PeripheralManager
import models.signals.SignalMessage
import java.io.BufferedReader


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
            throw SignalStreamTimingException(
                "Not all Signals were handled from the messageBuffer of the previous time step.")
        }
        messageBuffer = ArrayList()

        val toReturn: ArrayList<SignalMessage> = ArrayList(remnantsFromLastCall)

        // Keep reading new signals until we reach a point where the signal time is simply too high.
        var currentLine = reader.readLine()
        if (currentLine != null) {
            var currentMessage = SignalMessage.fromString(currentLine.split(",", limit = 2)[1])
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
