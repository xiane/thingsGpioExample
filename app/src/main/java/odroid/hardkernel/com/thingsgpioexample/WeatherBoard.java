package odroid.hardkernel.com.thingsgpioexample;

import com.google.android.things.pio.PeripheralManager;

public class WeatherBoard {
    /* UV, Visible, IR */
    private SI1132 si1132;

    public WeatherBoard(PeripheralManager manager, String i2c) {
        try {
            si1132 = new SI1132(manager.openI2cDevice(i2c, SI1132.ADDRESS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean init() {
        try {
            si1132.init();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public short readUV() throws Exception {
        return si1132.readUV();
    }

    public double readVisible() throws Exception {
        return si1132.readVisible();
    }

    public double readIR() throws Exception {
        return si1132.readIR();
    }
}
