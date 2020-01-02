package odroid.hardkernel.com.WeatherBoard;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

public class SI1132 {
    private I2cDevice device;

    public static final byte ADDRESS = 0x60;

    private final byte ID = 0x32;
    /* Command */
    private final byte CMD_PARAM_QUERY = (byte) 0x80;
    private final byte CMD_PARAM_SET = (byte) 0xA0;
    private final byte CMD_NOP = 0x00;
    private final byte CMD_RESET = 0x01;
    private final byte CMD_BUSADDR = 0x02;
    private final byte CMD_GET_CAL = 0x12;
    private final byte CMD_ALS_FORCE = 0x06;
    private final byte CMD_ALS_PAUSE = 0x0A;
    private final byte CMD_ALS_AUTO = 0x0E;

    /* Parameters */
    private final byte PARAM_I2CADDR = 0x00;
    private final byte PARAM_CHLIST = 0x01;
    private final byte PARAM_CHLIST_ENUV = (byte) 0x80;
    private final byte PARAM_CHLIST_ENAUX = 0x40;
    private final byte PARAM_CHLIST_ENALSIR = 0x20;
    private final byte PARAM_CHLIST_ENALSVIS = 0x10;

    private final byte PARAM_ALSENCODING = 0x06;

    private final byte PARAM_ALSIRADCMUX = 0x0E;
    private final byte PARAM_AUXADCMUX = 0x0F;

    private final byte PARAM_ALSVISADCCOUNTER = 0x10;
    private final byte PARAM_ALSVISADCGAIN = 0x11;
    private final byte PARAM_ALSVISADCMISC = 0x12;
    private final byte PARAM_ALSVISADCMISC_VISRANGE = 0x20;

    private final byte PARAM_ALSIRADCCOUNTER = 0x1D;
    private final byte PARAM_ALSIRADCGAIN = 0x1E;
    private final byte PARAM_ALSIRADCMISC = 0x1F;
    private final byte PARAM_ALSIRADCMISC_RANGE = 0x20;

    private final byte PARAM_ADCCOUNTER_511CLK = 0x70;

    private final byte PARAM_ADCMUX_SMALLIR = 0x00;
    private final byte PARAM_ADCMUX_LARGEIR = 0x03;
    /* Reg */
    private final byte REG_PARTID = 0x00;
    private final byte REG_INTCFG = 0x03;
    private final byte REG_INTCFG_INTOE = 0x01;
    private final byte REG_IRQEN = 0x04;
    private final byte REG_IRQEN_ALSEVERYSAMPLE = 0x01;
    private final byte REG_IRQMODE1 = 0x05;
    private final byte REG_IRQMODE2 = 0x06;
    private final byte REG_HWKEY = 0x07;
    private final byte REG_MEASRATE0 = 0x08;
    private final byte REG_MEASRATE1 = 0x09;
    private final byte REG_UCOEF0 = 0x13;
    private final byte REG_UCOEF1 = 0x14;
    private final byte REG_UCOEF2 = 0x15;
    private final byte REG_UCOEF3 = 0x16;
    private final byte REG_PARAMWR = 0x17;
    private final byte REG_COMMAND = 0x18;
    private final byte REG_IRQSTAT = 0x21;

    public SI1132(I2cDevice device) {
        this.device = device;
    }
    
    public void init() throws IOException, InterruptedException {
        if (device.readRegByte(REG_PARTID) == ID) {
            reset();

            byte[] ucoef = {0x7B, 0x6B, 0x01, 0x00};
            device.writeRegBuffer(REG_UCOEF0, ucoef, 4);

            byte chlist = (byte) (PARAM_CHLIST_ENUV |
                    PARAM_CHLIST_ENALSIR |
                    PARAM_CHLIST_ENALSVIS);
            writeParam(PARAM_CHLIST, chlist);

            device.writeRegByte(REG_INTCFG, REG_INTCFG_INTOE);
            device.writeRegByte(REG_IRQEN, REG_IRQEN_ALSEVERYSAMPLE);

            writeParam(PARAM_ALSIRADCMUX, PARAM_ADCMUX_SMALLIR);
            Thread.sleep(10);
            // fastest clocks, clock div 1
            writeParam(PARAM_ALSIRADCGAIN, 0x00);
            Thread.sleep(10);
            // take 511 clocks to measure
            writeParam(PARAM_ALSIRADCCOUNTER, PARAM_ADCCOUNTER_511CLK);
            //in high range mode
            writeParam(PARAM_ALSIRADCMISC, PARAM_ALSIRADCMISC_RANGE);
            Thread.sleep(10);
            // fastest clocks
            writeParam(PARAM_ALSVISADCGAIN, 0);
            Thread.sleep(10);
            // take 511 clocks to measure
            writeParam(PARAM_ALSVISADCCOUNTER, PARAM_ADCCOUNTER_511CLK);
            // in high range mode (not normal signal)
            writeParam(PARAM_ALSVISADCMISC, PARAM_ALSVISADCMISC_VISRANGE);
            Thread.sleep(10);
            device.writeRegByte(REG_MEASRATE0, (byte) 0xFF);
            device.writeRegByte(REG_COMMAND, CMD_ALS_AUTO);
        }
    }

    private void reset() throws IOException, InterruptedException {
        device.writeRegByte(REG_MEASRATE0, (byte) 0);
        device.writeRegByte(REG_MEASRATE1, (byte) 0);
        device.writeRegByte(REG_IRQEN, (byte) 0);
        device.writeRegByte(REG_IRQMODE1, (byte) 0);
        device.writeRegByte(REG_IRQMODE2, (byte) 0);
        device.writeRegByte(REG_INTCFG, (byte) 0);
        device.writeRegByte(REG_IRQSTAT, (byte) 0xFF);

        device.writeRegByte(REG_COMMAND, CMD_RESET);
        Thread.sleep(10);
        device.writeRegByte(REG_HWKEY, (byte) 0x17);
    }
    
    private void writeParam(int param, int val) {
        try {
            device.writeRegByte(REG_PARAMWR, (byte) val);
            device.writeRegByte(REG_COMMAND, (byte)(param | CMD_PARAM_SET));
        } catch (IOException e) {}
    }

    public short readUV() throws IOException, InterruptedException {
        Thread.sleep(10);
        return device.readRegWord(0x2C);
    }

    public double readVisible() throws IOException, InterruptedException {
        Thread.sleep(10);
        return ((device.readRegWord(0x22) - 256)/0.282) * 14.5;
    }

    public double readIR() throws IOException, InterruptedException {
        Thread.sleep(10);
        return ((device.readRegWord(0x24) - 250) / 2.44) * 14.5;
    }
}