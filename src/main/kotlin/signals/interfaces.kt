package signals

interface IConsumesGpioSignal {
    fun filterGpioSignal(): (GpioSignal) -> Boolean
}