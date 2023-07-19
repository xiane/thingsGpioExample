package odroid.hardkernel.com.BarcodeScanner;

import android.os.Build;

public class BoardDefaults {
    private static final String DEVICE_ODROIDN2 = "odroidn2";
    private static final String DEVICE_ODROIDN2L = "odroidn2l";
    private static final String DEVICE_ODROIDC4 = "odroidc4";
    private static final String DEVICE_ODROIDM1 = "odroidm1";
    /**
     * Return the preferred I2C port for each board.
     */
    public static String getUARTPort() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "UART-1";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public static String getTriggerPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "18";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public static String getResetPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "16";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
