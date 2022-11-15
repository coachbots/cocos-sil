package models.signals

import models.signals.gpio.GpioSignal

/**
 * Represents a signal message with data required to uniquely identify it. A signal message is effectively a signal
 * associated with a fire timestamp and a string body.
 *
 * @author Marko Vejnovic <contact@markovejnovic.com>
 */
data class SignalMessage(val timestamp: Float, val signalType: SignalType, val rawBody: String) {
    /**
     * The signal cast down to a correct type.
     */
    val signal: ISignal
        get() {
        when (signalType) {
            SignalType.GPIO -> { return GpioSignal.fromString(rawBody) }
        }
    }

    companion object {
        /**
         * Given a string, this function will create a SignalMessage corresponding to said string.
         * The string must be a CSV-formatted string of the form:
         * <TIMESTAMP: ULong>,<SIGNAL_TYPE: Int>,<Signal Body...>
         */
        fun fromString(string: String): SignalMessage {
            val splitString = string.split(",", limit = 3)
            return SignalMessage(splitString[0].toFloat(), SignalType.fromInt(splitString[1].toInt()), splitString[2])
        }
    }
}