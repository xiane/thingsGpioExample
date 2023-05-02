package odroid.hardkernel.com.RotaryEncoderNServoMotor;

import android.os.Build;

@SuppressWarnings("WeakerAccess")

public class BoardDefaults {
    private static final String DEVICE_ODROIDN2 = "odroidn2";
    private static final String DEVICE_ODROIDN2L = "odroidn2l";
    private static final String DEVICE_ODROIDC4 = "odroidc4";
    private static final String DEVICE_ODROIDM1 = "odroidm1";

    /**
     * Return the preferred PWM Pin for each board.
     */
    public static String getServoMotorPwm() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDM1:
                return "7";
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "12";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    /**
     * Return the preferred DT GPIO Pin for each board.
     */
    public static String getDtPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDM1:
                return "12";
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "13";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public static String getSwPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDM1:
                return "16";
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "11";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }

    public static String getClkPin() {
        switch (Build.DEVICE) {
            case DEVICE_ODROIDM1:
                return "18";
            case DEVICE_ODROIDC4:
            case DEVICE_ODROIDN2:
            case DEVICE_ODROIDN2L:
                return "7";
            default:
                throw new IllegalStateException("Unknown Build.DEVICE " + Build.DEVICE);
        }
    }
}
