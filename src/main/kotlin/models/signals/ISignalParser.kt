package models.signals

/**
 * Describes a parser object that, given a string of a signal returns the signal.
 */
interface ISignalParser {
    fun fromString(signalBody: String): ISignal
}