package models.signals

/**
 * Describes a parser object that, given a string of a signal returns the signal.
 *
 * @author Marko Vejnovic <contact@markovejnovic.com>
 */
interface ISignalParser {
    fun fromString(signalBody: String): ISignal
}