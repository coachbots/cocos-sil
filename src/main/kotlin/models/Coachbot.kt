package models

import dev.romainguy.kotlin.math.Float2
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.PI
import dev.romainguy.kotlin.math.times
import models.peripherals.Gpio
import kotlin.math.atan
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class Coachbot(private val gpioPeripheral: Gpio) {
    private val leftMotorModel = MotorLeft(gpioPeripheral)
    private val rightMotorModel = MotorRight(gpioPeripheral)
    private val ledModel = LedModel(gpioPeripheral)

    var posCenter = Float2(0F, 0F)
    var theta = 0F
    val ledColor: Float3 get() = ledModel.color

    fun onTick(currentTime: Float, deltaTime: Float) {
        val leftMotorDeltaMeters = getWheelStep(leftMotorModel.onTick(currentTime, deltaTime))
        val rightMotorDeltaMeters = getWheelStep(rightMotorModel.onTick(currentTime, deltaTime))

        ledModel.onTick(currentTime, deltaTime)

        updatePositionTick(leftMotorDeltaMeters, rightMotorDeltaMeters)
    }

    companion object {
        const val COACHBOT_RADIUS: Float = 2E-2F
        const val WHEEL_RADIUS: Float = 2E-2F

        /**
         * Parameter used for tuning slippage. Valid range is between 0-1, set to lower values to increase slippage.
         */
        const val SLIPPAGE_COEFF: Float = 1.0F
    }

    /**
     * Returns the wheel step in meters. Assumes no slippage
     */
    private fun getWheelStep(deltaWheelAngle: Float): Float {
        return SLIPPAGE_COEFF * WHEEL_RADIUS * deltaWheelAngle;
    }

    private fun updatePositionTick(leftMotorDeltaMeters: Float, rightMotorDeltaMeters: Float) {
        // First, let us get the initial position of the wheels. Some linear algebra shows that this is
        // <left> = <bot_center> + bot_radius * <cos(bot_angle + pi / 2), sin(bot_angle + pi / 2)>
        // <right> = <bot_center> + bot_radius * <cos(bot_angle - pi / 2), sin(bot_angle - pi / 2)>
        val leftWheelPos = posCenter + COACHBOT_RADIUS * Float2(cos(theta + PI / 2F), sin(theta + PI / 2F))
        val rightWheelPos = posCenter + COACHBOT_RADIUS * Float2(cos(theta - PI / 2F), sin(theta - PI / 2F))
        // Now, these are the initial positions. Each said vector needs to be added with the motion vectors.
        val leftMotionVec = leftMotorDeltaMeters * Float2(cos(theta), sin(theta))
        val rightMotionVec = rightMotorDeltaMeters * Float2(cos(theta), sin(theta))

        val leftOutPos = leftWheelPos + leftMotionVec
        val rightOutPos = rightWheelPos + rightMotionVec

        // Now we know where each wheel lands, we can calculate the theta and position.
        // The position is trivially (<right> - <left>) / 2 * <left> and the theta is simply the angle normal to the
        // angle of <left> -> <right>
        val leftToRight = rightOutPos - leftOutPos
        val botCenter = leftToRight / 2F + leftOutPos
        val botTheta = atan2(leftToRight.y, leftToRight.x) + PI / 2

        posCenter = botCenter
        theta = botTheta
    }
}