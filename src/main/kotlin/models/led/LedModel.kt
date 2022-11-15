package models.led

import models.Color
import models.peripherals.gpio.GpioPeripheral

/**
 * Represents an LED attached to the Coachbot.
 *
 * Due to the fact that the LED is controlled via software PWM (so, GPIO), this model attempts to compensate for this
 * by keeping a tally count of high GPIO signals and low GPIO signals and returning the brightness of each channel to be
 * the ratio of the two.
 *
 * @author Marko Vejnovic <contact@markovejnovic.com>
 */
class LedModel(private val gpio: GpioPeripheral) {
    var color = Color(0F, 0F, 0F)

    // TODO: These might overflow for extremely high tick rates.
    private var softwarePwmCompensators = mapOf(
        "PIN_R" to arrayOf(0UL, 0UL),
        "PIN_G" to arrayOf(0UL, 0UL),
        "PIN_B" to arrayOf(0UL, 0UL)
    )

    fun onTick(currentTime: Float, nextTime: Float) {
        // In order to update the color we need to fake color. Assuming a duty cycle of 100%, the color should be 255,
        // but as you will note here, we're not injecting a PWM driver -- software PWM is used. In order to emulate
        // the behavior of that:
        // For every high GPIO tick, let us increment an accumulator and for a low GPIO tick, let us increment another
        // The duty cycle is then simply the ratio of the two.
        for (pin in PWM_PIN_MAP.keys) {
            if (gpio[PWM_PIN_MAP[pin]!!].isHigh) {
                softwarePwmCompensators[pin]!![1]++
            } else {
                softwarePwmCompensators[pin]!![0]++;
            }
        }

        fun calculateDc(pinName: String): Float {
            val countHigh = softwarePwmCompensators[pinName]!![1]
            val countLow = softwarePwmCompensators[pinName]!![0]
            val countTotal = countHigh + countLow
            return (countHigh.toFloat() / countTotal.toFloat())
        }

        color = Color(calculateDc("PIN_R"), calculateDc("PIN_G"), calculateDc("PIN_B"))
    }

    companion object {
        val PWM_PIN_MAP = mapOf(
            "PIN_R" to 15,
            "PIN_G" to 16,
            "PIN_B" to 18
        )
    }
}