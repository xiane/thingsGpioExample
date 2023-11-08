package odroid.hardkernel.com.Mcp2515;

import android.os.Build;

@SuppressWarnings("WeakerAccess")

public class BoardDefaults {
    private static final String DEVICE_ODROIDN2 = "odroidn2";
    private static final String DEVICE_ODROIDN2L = "odroidn2l";
    private static final String DEVICE_ODROIDC4 = "odroidc4";
    private static final String DEVICE_ODROIDM1 = "odroidm1";
    private static final String DEVICE_ODROIDM1S = "odroidm1s";

    /**
     * Return the preferred SPI port for each board.
     */
    public static String getSPIPort() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDM1S:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "SPI0.0";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the preferred GPIO Pin for each board.
     */
    public static String getInterruptPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
                return "13";
            case DEVICE_ODROIDM1S:
            case DEVICE_ODROIDM1:
                return "7";
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "11";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
