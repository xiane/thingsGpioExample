package com.hardkernel.drivers.led

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import java.io.IOException

class LED: AutoCloseable {
    private var pin: Gpio? = null

    /**
     * Get a gpio instance and set the direction.
     */
    @Throws(IOException::class)
    constructor(pinName: String) {
        pin = PeripheralManager.getInstance()
                .openGpio(pinName)
        pin?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)
    }

    /**
     * Turn on when true, and turn off when false.
     */
    @Throws(Exception::class)
    fun turn(on: Boolean) {

        pin?.value = on
    }

    /**
     * auto close the gpio pin.
     */
    @Throws(IOException::class)
    override fun close() {
        pin?.close()
    }
}