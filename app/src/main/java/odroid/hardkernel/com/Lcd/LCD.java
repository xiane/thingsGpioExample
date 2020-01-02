package odroid.hardkernel.com.Lcd;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.util.List;

public class LCD {
    private I2cDevice device;

    final private static int I2C_ADDR = 0x27;
    final private static int LCD_WIDTH = 16;
    final private static byte LCD_CHR = 1;
    final private static byte LCD_CMD = 0;
    final private static byte LCD_BACKLIGHT = 0x08;
    final private static byte ENABLE = 0b00000100;

    final public static byte LINE_1 = (byte) 0x80;
    final public static byte LINE_2 = (byte) 0xC0;
    final public static byte LINE_3 = (byte) 0x94;
    final public static byte LINE_4 = (byte) 0xD4;

    public LCD() throws Exception {
        // get Peripheral Manager for managing the i2c device.
        PeripheralManager manager = PeripheralManager.getInstance();

        /*
          get available i2c pin list.
          i2c name format - i2c-#, and n2 have i2c-2 and i2c-3.
          In this case, i2c-2 is used.
         */
        List<String> i2cBusList = manager.getI2cBusList();
        device = manager.openI2cDevice(i2cBusList.get(0), I2C_ADDR);
    }

    public void init() throws Exception {
        lcd_byte(0x33, LCD_CMD); // 0b110011 Initialise
        lcd_byte(0x32, LCD_CMD); // 0b110010 Initialise
        lcd_byte(0x06, LCD_CMD); // 0b000110 Cursor move direction
        lcd_byte(0x0C, LCD_CMD); // 0b001100 Display On, Cursor Off, Blink Off
        lcd_byte(0x28, LCD_CMD); // 0b101000 Data length, Number of Lines, Font size
        lcd_byte(0x01, LCD_CMD); // 0b000001 Clear Display
        Thread.sleep(5);
    }

    private void lcd_byte(int bits, byte mode) throws Exception {
        byte bits_high = (byte) (mode | (bits & 0xF0) | LCD_BACKLIGHT);
        byte bits_low = (byte) (mode | (bits << 4) & 0xF0 | LCD_BACKLIGHT);
        device.writeRegByte(0, bits_high);
        lcd_toogle_enable(bits_high);
        device.writeRegByte(0, bits_low);
        lcd_toogle_enable(bits_low);
    }

    private void lcd_toogle_enable(byte bits) throws Exception {
        device.writeRegByte(0, (byte) (bits | ENABLE));
        device.writeRegByte(0, (byte) (bits & ~ENABLE));
    }

    public void print(String msg, byte line) throws Exception {
        lcd_byte(line, LCD_CMD);
        for(char ch: msg.substring(0, LCD_WIDTH).toCharArray()) {
            lcd_byte(ch, LCD_CHR);
        }
    }
}
