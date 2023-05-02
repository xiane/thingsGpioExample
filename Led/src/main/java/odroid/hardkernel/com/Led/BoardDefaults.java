package odroid.hardkernel.com.Led;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class BoardDefaults {
    private static final String DEVICE_ODROIDN2 = "odroidn2";
    private static final String DEVICE_ODROIDN2L = "odroidn2l";
    private static final String DEVICE_ODROIDC4 = "odroidc4";
    private static final String DEVICE_ODROIDM1 = "odroidm1";
    /**
     * Return the preferred GPIO Pin for each board.
     */
    public static String getLedPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
                return "13";
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "7";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
