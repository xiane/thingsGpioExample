package odroid.hardkernel.com.Lcd;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.util.List;

public class LCD {
    private I2cDevice device;

    final private static int I2C_ADDR = 0x27;
    final private static byte LCD_CHR = 1;
    final private static byte LCD_CMD = 0;
    final private static byte ENABLE = 0b00000100;

    // commands
    final private static int LCD_CLEARDISPLAY = 0x01;
    final private static int LCD_RETURNHOME = 0x02;
    final private static int LCD_ENTRYMODESET = 0x04;
    final private static int LCD_DISPLAYCONTROL = 0x08;
    final private static int LCD_CURSORSHIFT = 0x10;
    final private static int LCD_FUNCTIONSET = 0x20;
    final private static int LCD_SETCGRAMADDR = 0x40;
    final private static int LCD_SETDDRAMADDR = 0x80;
    // flags for display entry mode
    final private static int LCD_ENTRYRIGHT = 0x00;
    final private static int LCD_ENTRYLEFT = 0x02;
    final private static int LCD_ENTRYSHIFTINCREMENT = 0x01;
    final private static int LCD_ENTRYSHIFTDECREMENT = 0x00;

    // flags for display on/off control
    final private static int LCD_DISPLAYON = 0x04;
    final private static int LCD_DISPLAYOFF = 0x00;
    final private static int LCD_CURSORON = 0x02;
    final private static int LCD_CURSOROFF = 0x00;
    final private static int LCD_BLINKON = 0x01;
    final private static int LCD_BLINKOFF = 0x00;

    // flags for display/cursor shift
    final private static int LCD_DISPLAYMOVE = 0x08;
    final private static int LCD_CURSORMOVE = 0x00;
    final private static int LCD_MOVERIGHT = 0x04;
    final private static int LCD_MOVELEFT = 0x00;

    // flags for function set
    final private static int LCD_8BITMODE = 0x10;
    final private static int LCD_4BITMODE = 0x00;
    final private static int LCD_2LINE = 0x08;
    final private static int LCD_1LINE = 0x00;
    final private static int LCD_5x10DOTS = 0x04;
    final private static int LCD_5x8DOTS = 0x00;

    // flags for backlight control
    final private static int LCD_BACKLIGHT = 0x08;
    final private static int LCD_NOBACKLIGHT = 0x00;

    private final static byte LINE_1 = (byte) 0x80;
    private final static byte LINE_2 = (byte) 0xC0;
    private final static byte LINE_3 = (byte) 0x94;
    private final static byte LINE_4 = (byte) 0xD4;

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
        write_cmd(0x03);
        write_cmd(0x03);
        write_cmd(0x03);
        write_cmd(0x02);

        write_cmd(LCD_ENTRYMODESET | LCD_ENTRYLEFT);
        write_cmd(LCD_FUNCTIONSET | LCD_2LINE | LCD_5x8DOTS | LCD_4BITMODE);
        write_cmd(LCD_DISPLAYCONTROL | LCD_DISPLAYON);
        write_cmd(LCD_CLEARDISPLAY);
        Thread.sleep(5);
    }

    private void write_cmd(int bits) throws Exception {
        lcd_byte(bits, LCD_CMD);
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

    public void print(String msg, int line) throws Exception {
        switch (line) {
            case 1:
                write_cmd(LINE_1);
                break;
            case 2:
                write_cmd(LINE_2);
                break;
            case 3:
                write_cmd(LINE_3);
                break;
            case 4:
                write_cmd(LINE_4);
                break;
        }

        for(char ch: msg.toCharArray()) {
            lcd_byte(ch, LCD_CHR);
        }
    }
}
