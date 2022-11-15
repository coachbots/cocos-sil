package models.peripherals.gpio

data class GpioState(var isHigh: Boolean, val direction: GpioDirection, val pullMode: GpioPullMode) {
    companion object {
        fun empty(): GpioState {
            return GpioState(false, GpioDirection.OUT, GpioPullMode.UP);
        }
    }
}