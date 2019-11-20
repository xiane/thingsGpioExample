package odroid.hardkernel.com.thingsgpioexample;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

public class WeatherBoard {
    private  I2cDevice device;
    private final byte ID_SI1132 = 0x32;

    public static final byte SI1132_ID = 0x60;

    /* Command */
    final byte SI1132_CMD_PARAM_QUERY = (byte) 0x80;
    final byte SI1132_CMD_PARAM_SET = (byte) 0xA0;
    final byte SI1132_CMD_NOP = 0x00;
    final byte SI1132_CMD_RESET = 0x01;
    final byte SI1132_CMD_BUSADDR = 0x02;
    final byte SI1132_CMD_GET_CAL = 0x12;
    final byte SI1132_CMD_ALS_FORCE = 0x06;
    final byte SI1132_CMD_ALS_PAUSE = 0x0A;
    final byte SI1132_CMD_ALS_AUTO = 0x0E;

    /* Parameters */
    final byte SI1132_PARAM_I2CADDR = 0x00;
    final byte SI1132_PARAM_CHLIST = 0x01;
    final byte SI1132_PARAM_CHLIST_ENUV = (byte) 0x80;
    final byte SI1132_PARAM_CHLIST_ENAUX = 0x40;
    final byte SI1132_PARAM_CHLIST_ENALSIR = 0x20;
    final byte SI1132_PARAM_CHLIST_ENALSVIS = 0x10;

    final byte SI1132_PARAM_ALSENCODING = 0x06;

    final byte SI1132_PARAM_ALSIRADCMUX = 0x0E;
    final byte SI1132_PARAM_AUXADCMUX = 0x0F;

    final byte SI1132_PARAM_ALSVISADCCOUNTER = 0x10;
    final byte SI1132_PARAM_ALSVISADCGAIN = 0x11;
    final byte SI1132_PARAM_ALSVISADCMISC = 0x12;
    final byte SI1132_PARAM_ALSVISADCMISC_VISRANGE = 0x20;

    final byte SI1132_PARAM_ALSIRADCCOUNTER = 0x1D;
    final byte SI1132_PARAM_ALSIRADCGAIN = 0x1E;
    final byte SI1132_PARAM_ALSIRADCMISC = 0x1F;
    final byte SI1132_PARAM_ALSIRADCMISC_RANGE = 0x20;

    final byte SI1132_PARAM_ADCCOUNTER_511CLK = 0x70;

    final byte SI1132_PARAM_ADCMUX_SMALLIR = 0x00;
    final byte SI1132_PARAM_ADCMUX_LARGEIR = 0x03;
    /* Reg */
    final byte SI1132_REG_PARTID = 0x00;
    final byte SI1132_REG_INTCFG = 0x03;
    final byte SI1132_REG_INTCFG_INTOE = 0x01;
    final byte SI1132_REG_IRQEN = 0x04;
    final byte SI1132_REG_IRQEN_ALSEVERYSAMPLE = 0x01;
    final byte SI1132_REG_IRQMODE1 = 0x05;
    final byte SI1132_REG_IRQMODE2 = 0x06;
    final byte SI1132_REG_HWKEY = 0x07;
    final byte SI1132_REG_MEASRATE0 = 0x08;
    final byte SI1132_REG_MEASRATE1 = 0x09;
    final byte SI1132_REG_UCOEF0 = 0x13;
    final byte SI1132_REG_UCOEF1 = 0x14;
    final byte SI1132_REG_UCOEF2 = 0x15;
    final byte SI1132_REG_UCOEF3 = 0x16;
    final byte SI1132_REG_PARAMWR = 0x17;
    final byte SI1132_REG_COMMAND = 0x18;
    final byte SI1132_REG_IRQSTAT = 0x21;

    public WeatherBoard(I2cDevice i2c) {
        device = i2c;
    }

    public boolean init() {
        try {
            byte id = device.readRegByte(SI1132_REG_PARTID);
            if (id == ID_SI1132) {
                initSi1132();
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void resetSi1132() throws Exception {
        device.writeRegByte(SI1132_REG_MEASRATE0, (byte) 0);
        device.writeRegByte(SI1132_REG_MEASRATE1, (byte) 0);
        device.writeRegByte(SI1132_REG_IRQEN, (byte) 0);
        device.writeRegByte(SI1132_REG_IRQMODE1, (byte) 0);
        device.writeRegByte(SI1132_REG_IRQMODE2, (byte) 0);
        device.writeRegByte(SI1132_REG_INTCFG, (byte) 0);
        device.writeRegByte(SI1132_REG_IRQSTAT, (byte) 0xFF);

        device.writeRegByte(SI1132_REG_COMMAND, SI1132_CMD_RESET);
        Thread.sleep(10);
        device.writeRegByte(SI1132_REG_HWKEY, (byte) 0x17);
    }

    private void initSi1132() throws Exception {
        resetSi1132();

        byte[] ucoef = {0x7B, 0x6B, 0x01, 0x00};
        device.writeRegBuffer(SI1132_REG_UCOEF0, ucoef, 4);

        byte chlist = (byte)(SI1132_PARAM_CHLIST_ENUV|
                SI1132_PARAM_CHLIST_ENALSIR|
                SI1132_PARAM_CHLIST_ENALSVIS);
        writeParam(SI1132_PARAM_CHLIST, chlist);

        device.writeRegByte(SI1132_REG_INTCFG, SI1132_REG_INTCFG_INTOE);
        device.writeRegByte(SI1132_REG_IRQEN, SI1132_REG_IRQEN_ALSEVERYSAMPLE);

        writeParam(SI1132_PARAM_ALSIRADCMUX, SI1132_PARAM_ADCMUX_SMALLIR);
        Thread.sleep(10);
        // fastest clocks, clock div 1
        writeParam(SI1132_PARAM_ALSIRADCGAIN, 0x00);
        Thread.sleep(10);
        // take 511 clocks to measure
        writeParam(SI1132_PARAM_ALSIRADCCOUNTER, SI1132_PARAM_ADCCOUNTER_511CLK);
        //in high range mode
        writeParam(SI1132_PARAM_ALSIRADCMISC, SI1132_PARAM_ALSIRADCMISC_RANGE);
        Thread.sleep(10);
        // fastest clocks
        writeParam(SI1132_PARAM_ALSVISADCGAIN, 0);
        Thread.sleep(10);
        // take 511 clocks to measure
        writeParam(SI1132_PARAM_ALSVISADCCOUNTER, SI1132_PARAM_ADCCOUNTER_511CLK);
        // in high range mode (not normal signal)
        writeParam(SI1132_PARAM_ALSVISADCMISC, SI1132_PARAM_ALSVISADCMISC_VISRANGE);
        Thread.sleep(10);
        device.writeRegByte(SI1132_REG_MEASRATE0, (byte)0xFF);
        device.writeRegByte(SI1132_REG_COMMAND, SI1132_CMD_ALS_AUTO);
    }

    private void writeParam(int param, int val) {
        try {
            device.writeRegByte(SI1132_REG_PARAMWR, (byte) val);
            device.writeRegByte(SI1132_REG_COMMAND, (byte)(param | SI1132_CMD_PARAM_SET));
        } catch (IOException e) {}
    }

    public short readUV() throws Exception {
        Thread.sleep(10);
        return device.readRegWord(0x2C);
    }

    public double readVisible() throws Exception {
        Thread.sleep(10);
        return ((device.readRegWord(0x22) - 256)/0.282) * 14.5;
    }

    public double readIR() throws Exception {
        Thread.sleep(10);
        return ((device.readRegWord(0x24) - 250) / 2.44) * 14.5;
    }
}
