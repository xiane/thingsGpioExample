package odroid.hardkernel.com.Lcd;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class BoardDefaults {
    private static final String DEVICE_ODROIDN2 = "odroidn2";
    private static final String DEVICE_ODROIDN2L = "odroidn2l";
    private static final String DEVICE_ODROIDC4 = "odroidc4";
    private static final String DEVICE_ODROIDM1 = "odroidm1";
    private static final String DEVICE_ODROIDM1S = "odroidm1s";
    /**
     * Return the preferred I2C port for each board.
     */
    public static String getI2CPort() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDM1S:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "I2C-1";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public static String[] getAddressGpio() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
                return new String[] {"16", "18", "22"};
            case DEVICE_ODROIDM1S:
            case DEVICE_ODROIDM1:
                return new String[] {"12", "16", "18"};
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return new String[] {"7", "16", "18"};
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
