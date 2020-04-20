package odroid.hardkernel.com.max30100;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayDeque;
import java.util.List;

import java.nio.ByteBuffer;

public class Max30100 {
    /*
     * 8bit I2C address of the MAX30100 with R/W bit set
     */
    static final byte MAX30100_ADDR = 0x57;

    private static class REG {
        static final byte INT_STATUS = 0x00; // Which interrupts are tripped
        static final byte INT_ENABLE = 0x01; // Which interrupts are active
        static final byte FIFO_WR_PTR = 0x02; // Where data is being written
        static final byte OVRFLOW_CTR = 0x03; // Number of lost samples
        static final byte FIFO_RD_PTR = 0x04; // Where to read from
        static final byte FIFO_DATA = 0x05; // Ouput data buffer
        static final byte MODE_CONFIG = 0x06; // Control register
        static final byte SPO2_CONFIG = 0x07; // Oximetry settings
        static final byte LED_CONFIG = 0x09; // Pulse width and power of LEDs
        static final byte TEMP_INTG = 0x16; // Temperature value, whole number
        static final byte TEMP_FRAC = 0x17; // Temperature value, fraction
        static final int REV_ID = 0xFE; // Part revision
        static final int PART_ID = 0xFF; // Part ID, normally 0x11
    }

    /*
     * Mode Configuration
     */
    private static final byte MODE_CONFIG_TEMP_EN = (1 << 3);
    private static final byte MODE_CONFIG_RESET = (1 << 6);
    private static final byte MODE_CONFIG_SHUTDOWN = (byte)(1 << 7);

    /*
     * SpO2 Configuration
     */
    private static final int SPC_SPO2_HI_RES_EN = (1 << 6);

    private static final int FIFO_DEPTH = 0x10;

    public static final int PART_ID = 0x11;

    public enum modeControl {
        HR, // IR only - 0x02
        SPO2_HR // RED and IR - 0x03
    }

    public enum pulseWidth {
        us200, // 200us pulse
        us400, // 400us pulse
        us800, // 800us pulse
        us1600 // 1600us pulse
    }

    public enum sampleRate {
        hz50,    // 50 Hz
        hz100,   // 100 Hz
        hz167,   // 167 Hz
        hz200,   // 200 Hz
        hz400,   // 400 Hz
        hz600,   // 600 Hz
        hz800,   // 800 Hz
        hz1000   // 1000 Hz
    }

    public enum ledCurrent {
        i0,    // No current
        i4,    // 4.4mA
        i8,    // 7.6mA
        i11,   // 11.0mA
        i14,   // 14.2mA
        i17,   // 17.4mA
        i21,   // 20.8mA
        i24,   // 24 mA
        i27,   // 27.1mA
        i31,   // 30.6mA
        i34,   // 33.8mA
        i37,   // 37.0mA
        i40,   // 40.2mA
        i44,   // 43.6mA
        i47,   // 46.8mA
        i50    // 50.0mA
    }

    private I2cDevice i2c;
    private float temperature;

    private byte[] buffer = new byte[FIFO_DEPTH * 4];
    private ArrayDeque<Integer> rawIR = new ArrayDeque<>();
    private ArrayDeque<Integer> rawRED = new ArrayDeque<>();

    public Max30100(String i2c) throws IOException{
        PeripheralManager manager = PeripheralManager.getInstance();
        List<String> i2cList = manager.getI2cBusList();

        if (i2cList.contains(i2c))
            this.i2c = manager.openI2cDevice(i2c, MAX30100_ADDR);
        else
            this.i2c = manager.openI2cDevice(i2cList.get(0), MAX30100_ADDR);
    }

    public float getTemperatureC() {
        return temperature;
    }

    public float getTemperatureF() {
        return temperature * (9/5) + 32;
    }

    public int update() throws IOException {
        return readFIFO();
    }

    public int getIR() {
        return rawIR.pop();
    }

    public int getRED() {
        return rawRED.pop();
    }

    public void initialize() throws IOException {

        int partId = getPartId();

        if (partId != PART_ID)
            throw new IOException("partId is not expected - "  + partId);
        begin(modeControl.HR,
                pulseWidth.us1600,
                ledCurrent.i50,
                ledCurrent.i50,
                sampleRate.hz100);
        setHiResModeEnable(true);
    }

    public void begin(modeControl mc, pulseWidth width, ledCurrent ir, ledCurrent red, sampleRate rate) throws IOException{
        setMode(mc);
        setLedCurrent(ir, red);
        setLedPulseWidth(width);
        setSamplingRate(rate);
    }

    public void setMode(modeControl mode) throws IOException {
        i2c.writeRegByte(REG.MODE_CONFIG, (byte)(mode.ordinal() + 0x02));
    }

    public void setLedPulseWidth(pulseWidth width) throws IOException {
        byte data = i2c.readRegByte(REG.SPO2_CONFIG);
        int previous = data & 0xff;

        int appliedConfig = (previous & 0xfc) | width.ordinal();

        i2c.writeRegByte(REG.SPO2_CONFIG, (byte)appliedConfig);
    }

    public void setSamplingRate(sampleRate rate) throws IOException {
        byte data =i2c.readRegByte(REG.SPO2_CONFIG);
        int previous = data & 0xff;

        int appliedConfig = (previous & 0xe3) | (rate.ordinal() << 2);

        i2c.writeRegByte(REG.SPO2_CONFIG, (byte)appliedConfig);
    }

    public void setLedCurrent(ledCurrent ir, ledCurrent red) throws IOException {
        byte data = (byte)((red.ordinal() <<4) | ir.ordinal());
        i2c.writeRegByte(REG.LED_CONFIG, data);
    }

    public void setLedCurrent(int irIdx, int redIdx) throws IOException {
        byte data = (byte) ((redIdx << 4) | irIdx);
        i2c.writeRegByte(REG.LED_CONFIG, data);
    }

    public void setHiResModeEnable(boolean enabled) throws IOException {
        byte data = i2c.readRegByte(REG.SPO2_CONFIG);
        int previous = data & 0xff;

        int newValue;
        if (enabled)
            newValue = previous | SPC_SPO2_HI_RES_EN;
        else
            newValue = previous & ~SPC_SPO2_HI_RES_EN;

        i2c.writeRegByte(REG.SPO2_CONFIG, (byte) newValue);
    }

    public void startTemperatureSampling() throws IOException {
        byte data = i2c.readRegByte(REG.MODE_CONFIG);
        int modeConfig = data & 0xff;

        modeConfig |= MODE_CONFIG_TEMP_EN;

        i2c.writeRegByte(REG.MODE_CONFIG, (byte)modeConfig);
    }

    public boolean isTemperatureReady() throws IOException {
        byte data = i2c.readRegByte(REG.MODE_CONFIG);
        if (((data & 0xff) & MODE_CONFIG_TEMP_EN) > 0)
            return true;
        return false;
    }

    public void readTemperature() throws IOException {
        byte data;
        int tempInt, tempFrac;

        data = i2c.readRegByte(REG.TEMP_INTG);
        tempInt = data & 0xff;
        data = i2c.readRegByte(REG.TEMP_FRAC);
        tempFrac = data & 0xff;

        temperature = (float)((float)tempFrac * 0.0625 + tempInt);
    }

    public void shutdown() throws IOException {
        byte data = i2c.readRegByte(REG.MODE_CONFIG);
        int modeConfig = data & 0xff;

        modeConfig |= MODE_CONFIG_SHUTDOWN;

        i2c.writeRegByte(REG.MODE_CONFIG, (byte)modeConfig);
    }

    public void resume() throws IOException {
        byte data = i2c.readRegByte(REG.MODE_CONFIG);
        int modeConfig = data & 0xff;

        modeConfig &= ~MODE_CONFIG_SHUTDOWN;

        i2c.writeRegByte(REG.MODE_CONFIG, (byte)modeConfig);
    }

    public int getPartId() throws IOException {
        byte data = i2c.readRegByte(REG.PART_ID);
        return data & 0xff;
    }

    public void resetFifo() throws IOException {
        i2c.writeRegByte(REG.FIFO_WR_PTR, (byte)0);
        i2c.writeRegByte(REG.FIFO_RD_PTR, (byte)0);
        i2c.writeRegByte(REG.OVRFLOW_CTR, (byte)0);
    }

    private int readFIFO() throws IOException {
        byte data[] = new byte[4];

        i2c.readRegBuffer(REG.FIFO_WR_PTR, data, 3);
        int writePointer = data[0] & 0xff;
        int overflow = data[1] & 0xff;
        int readPointer = data[2] & 0xff;
        int toRead = (writePointer - readPointer) & (FIFO_DEPTH - 1);

        if (overflow > 0)
            toRead = FIFO_DEPTH;

        if (toRead > 0) {
            i2c.readRegBuffer(REG.FIFO_DATA, buffer, 4 * toRead);

            for (int i=0; i< toRead; i++) {
                int rawIRValue = ByteToShort(buffer, i * 4);
                int rawRedValue = ByteToShort(buffer, i * 4 + 2);

                rawIR.add(rawIRValue);
                rawRED.add(rawRedValue);
            }
        }
        return rawIR.size();
    }

    private static ByteBuffer byteBuffer = ByteBuffer.allocate(8);

    public static short ByteToShort(byte[] data, int offset) {

        byteBuffer.clear();
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.put(data, offset, 2);
        byteBuffer.position(0);

        return byteBuffer.getShort();
    }

    public void write_byte(char reg, char data) throws IOException {
        i2c.writeRegByte(reg, (byte) data);
    }
}
