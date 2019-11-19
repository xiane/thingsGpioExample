package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    PeripheralManager manager;
    final static int I2C_ADDR = 0x27;
    final static int LCD_WIDTH = 16;
    final static byte LCD_CHR = 1;
    final static byte LCD_CMD = 0;
    final static byte LCD_LINE_1 = (byte) 0x80;
    final static byte LCD_LINE_2 = (byte) 0xC0;
    final static byte LCD_LINE_3 = (byte) 0x94;
    final static byte LCD_LINE_4 = (byte) 0xD4;
    final static byte LCD_BACKLIGHT = 0x08;
    final static byte ENABLE = 0b00000100;

    I2cDevice device;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get Peripheral Manager for managing the gpio.
        manager = PeripheralManager.getInstance();

        List<String> i2cBusList = manager.getI2cBusList();

        try (I2cDevice i2c = manager.openI2cDevice(i2cBusList.get(0), I2C_ADDR)) {
            device = i2c;

            lcd_byte(0x33, LCD_CMD);
            lcd_byte(0x32, LCD_CMD);
            lcd_byte(0x06, LCD_CMD);
            lcd_byte(0x0C, LCD_CMD);
            lcd_byte(0x28, LCD_CMD);
            lcd_byte(0x01, LCD_CMD);
            Thread.sleep(5);

            while (true) {
                lcd_string("***ODROID-N2*** ", LCD_LINE_1);
                lcd_string("ODROID-magazine ", LCD_LINE_2);

                lcd_string("A speed is reliable?", LCD_LINE_3);
                lcd_string("Or is it really slow", LCD_LINE_4);
                Thread.sleep(3000);

                lcd_string("***HardKernel***", LCD_LINE_1);
                lcd_string("*hardkernel.com*", LCD_LINE_2);

                lcd_string("This is I2C test apk", LCD_LINE_3);
                lcd_string("4th line is work yeh", LCD_LINE_4);
                Thread.sleep(3000);
            }
        } catch (Exception e) {}
    }

    void lcd_byte(int bits, byte mode) throws Exception {
        byte bits_high = (byte) (mode | (bits & 0xF0) | LCD_BACKLIGHT);
        byte bits_low = (byte) (mode | (bits <<4) & 0xF0 | LCD_BACKLIGHT);
        device.writeRegByte(0, bits_high);
        lcd_toogle_enable(bits_high);
        device.writeRegByte(0, bits_low);
        lcd_toogle_enable(bits_low);
    }

    void lcd_toogle_enable(byte bits) throws Exception {
        device.writeRegByte(0, (byte) (bits|ENABLE));
        device.writeRegByte(0, (byte) (bits& ~ENABLE));
    }

    void lcd_string(String msg, byte line) throws Exception {
        lcd_byte(line, LCD_CMD);
        for(char ch: msg.toCharArray()) {
            lcd_byte(ch, LCD_CHR);
        }
    }
}