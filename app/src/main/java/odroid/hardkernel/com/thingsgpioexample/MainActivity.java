package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Gpio;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;
    Gpio gpio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get Peripheral Manager for managing the gpio.
        manager = PeripheralManager.getInstance();

        // get available gpio pin list.
        // each pin name is consist as P + physical pin number.
        List<String> gpioList = manager.getI2cBusList();

        for(String i2c: gpioList)
            Log.d("things", "i2c - " + i2c);

        try (I2cDevice i2c = manager.openI2cDevice(gpioList.get(0), 0x60)) {
            byte id = i2c.readRegByte(0);
            Log.d("i2c things", "id - " + id);
            if (id == 0x32) {
                byte[] ucoef = {0x7b, 0x6b, 0x01, 0x00};
                i2c.writeRegBuffer(0x13, ucoef, 4);

                byte chlist = (byte)(0x80|0x20|0x10);
                writeParam(i2c, (byte)0x01, chlist);

                i2c.writeRegByte(0x03, (byte) 0x01);
                i2c.writeRegByte(0x04, (byte)0x01);

                writeParam(i2c,(byte)0x0e, (byte) 0x00);
                Thread.sleep(10);
                writeParam(i2c, (byte)0x1e, (byte)0);
                Thread.sleep(10);
                writeParam(i2c, (byte)0x1d, (byte)0x70);
                writeParam(i2c, (byte)0x1F, (byte)0x20);
                Thread.sleep(10);
                writeParam(i2c,(byte)0x1e, (byte)0);
                Thread.sleep(10);
                writeParam(i2c, (byte)0x1d, (byte)0x70);
                writeParam(i2c, (byte)0x12, (byte)0x20);
                Thread.sleep(10);
                i2c.writeRegByte(0x08, (byte)0xff);
                i2c.writeRegByte(0x18,(byte)0x0E);
                while(true) {
                    Log.d("i2c things", "UV_idx - " + (readUV(i2c) / 100.0));
                    Log.d("i2c things", "visiable -" + readVisible(i2c) + " lux");
                    Log.d("i2c things", "IR - " + readIR(i2c) + " lux");
                    Thread.sleep(1000);
                }
            }
        } catch (Exception e) {}
    }
    void writeParam(I2cDevice device, byte param, byte val) {
        try {
            device.writeRegByte(0x17, val);
            device.writeRegByte(0x18, (byte)(param | (byte)0xA0));
        } catch (IOException e) {}
    }

    short readUV(I2cDevice device) throws Exception {
        Thread.sleep(10);
        return device.readRegWord(0x2c);
    }
    double readVisible(I2cDevice device) throws Exception {
        Thread.sleep(10);
        return ((device.readRegWord(0x22) - 256)/0.282) * 14.5;
    }
    double readIR(I2cDevice device) throws Exception {
        Thread.sleep(10);
        return ((device.readRegWord(0x24) - 250) / 2.44) * 14.5;
    }
}
