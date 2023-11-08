package odroid.hardkernel.com.Relay4ch;

import android.os.Build;

@SuppressWarnings("WeakerAccess")
public class BoardDefaults {
    private static final String DEVICE_ODROIDN2 = "odroidn2";
    private static final String DEVICE_ODROIDN2L = "odroidn2l";
    private static final String DEVICE_ODROIDC4 = "odroidc4";
    private static final String DEVICE_ODROIDM1 = "odroidm1";
    private static final String DEVICE_ODROIDM1S = "odroidm1s";

    public static String[] getRelayChGpios() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDM1S:
            case DEVICE_ODROIDM1:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return new String[] {"13", "11", "35", "33"};
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
