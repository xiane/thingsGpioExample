package odroid.hardkernel.com.rfid

import android.os.Build

object BoardDefaults {
    private const val DEVICE_ODROIDN2 = "odroidn2"
    private const val DEVICE_ODROIDN2L = "odroidn2l"
    private const val DEVICE_ODROIDC4 = "odroidc4"
    private const val DEVICE_ODROIDM1 = "odroidm1"
    private const val DEVICE_ODROIDM1S = "odroidm1s"
    val spiPort: String
        get() = when (Build.DEVICE) {
            DEVICE_ODROIDC4, DEVICE_ODROIDM1S, DEVICE_ODROIDM1, DEVICE_ODROIDN2, DEVICE_ODROIDN2L -> "SPI0.0"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
    val resetPin: String
        get() = when (Build.DEVICE) {
            DEVICE_ODROIDC4, DEVICE_ODROIDM1S, DEVICE_ODROIDM1, DEVICE_ODROIDN2, DEVICE_ODROIDN2L -> "22"
            else -> throw IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE)
        }
}