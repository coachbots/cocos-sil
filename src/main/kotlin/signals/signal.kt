package signals

data class Signal(val timestamp: ULong, val body: String) {
    companion object {
        fun fromString(string: String): Signal {
            val splitString = string.split(",", limit = 2)
            return Signal(splitString[0].toULong(), splitString[1])
        }
    }
}