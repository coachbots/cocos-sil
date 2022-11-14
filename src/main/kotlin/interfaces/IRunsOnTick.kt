package interfaces

interface IRunsOnTick {
    fun onTick(tickBegin: ULong, tickEnd: ULong)
}