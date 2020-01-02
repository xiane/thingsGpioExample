package odroid.hardkernel.com.WeatherBoard;

import com.google.android.things.pio.PeripheralManager;

import java.util.List;

public class WeatherBoard {
    /* Android Things Peripheral Manager */
    private PeripheralManager manager;

    /* UV, Visible, IR */
    private SI1132 si1132;
    /* Temperature, Humidity, Pressure, Altitude */
    private BME280 bme280;

    public WeatherBoard() {
        // get Peripheral Manager for managing the i2c.
        manager = PeripheralManager.getInstance();

        /*
          get available i2c pin list.
          i2c name format - i2c-#, and n2 have i2c-2 and i2c-3.
          In this case, i2c-2 is used.
         */
        List<String> i2cList = manager.getI2cBusList();
        String i2c = i2cList.get(0);

        try {
            si1132 = new SI1132(manager.openI2cDevice(i2c, SI1132.ADDRESS));
            bme280 = new BME280(manager.openI2cDevice(i2c, BME280.ADDRESS));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean init() {
        try {
            si1132.init();
            byte[] sampling = {BME280.POWER_MODE.NORMAL, 0x02, 0x02, 0x02};
            bme280.init(sampling);
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

    public double readHumidity() throws Exception {
        return bme280.readHumidity();
    }

    public double readTemperature() throws Exception {
        return bme280.readTemperature();
    }

    public double readPressure() throws Exception {
        return bme280.readPressure();
    }

    public double readAltitude(double pressure ,double seaLevel) {
        return 44330.0 * (1.0 - Math.pow(pressure/seaLevel, 0.1903));
    }
}
