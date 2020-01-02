package odroid.hardkernel.com.WeatherBoard;

import android.util.Log;

import com.google.android.things.pio.I2cDevice;

import java.io.IOException;

public class BME280 {
    private I2cDevice device;

    public static final byte ADDRESS = 0x76;

    private final byte ID = 0x60;

    public static class  POWER_MODE{
        final static byte SLEEP = 0b00;
        final static byte FORCE = 0b01;
        final static byte NORMAL = 0b11;
        final static byte SOFT_RESET_CODE = (byte) 0xB6;
    }

    private static class reg {
        final static byte CHIP_ID = (byte) 0xD0;
        final static byte RST = (byte) 0xE0;
        final static byte STAT = (byte) 0xF3;
        final static byte CTRL_MEAS = (byte) 0xF4;
        final static byte CTRL_HUMIDITY = (byte) 0xF2;
        final static byte CONFIG = (byte) 0xF5;

        static class pressure {
            final static byte MSB = (byte) 0xF7;
            final static byte LSB = (byte) 0xF8;
            final static byte XLSB = (byte) 0xF9;
        }

        static class temperature {
            final static byte MSB = (byte) 0xFA;
            final static byte LSB = (byte) 0xFB;
            final static byte XLSB = (byte) 0xFC;
        }

        static class humidity {
            final static byte MSB = (byte) 0xFD;
            final static byte LSB = (byte) 0xFE;
        }
    }

    private static class cali_reg {
        static class temperature {
            final static byte T1_LSB = (byte) 0x88;
            final static byte T1_MSB = (byte) 0x89;
            final static byte T2_LSB = (byte) 0x8A;
            final static byte T2_MSB = (byte) 0x8B;
            final static byte T3_LSB = (byte) 0x8C;
            final static byte T3_MSB = (byte) 0x8D;
        }

        static class pressure {
            final static byte P1_LSB = (byte) 0x8E;
            final static byte P1_MSB = (byte) 0x8F;
            final static byte P2_LSB = (byte) 0x90;
            final static byte P2_MSB = (byte) 0x91;
            final static byte P3_LSB = (byte) 0x92;
            final static byte P3_MSB = (byte) 0x93;
            final static byte P4_LSB = (byte) 0x94;
            final static byte P4_MSB = (byte) 0x95;
            final static byte P5_LSB = (byte) 0x96;
            final static byte P5_MSB = (byte) 0x97;
            final static byte P6_LSB = (byte) 0x98;
            final static byte P6_MSB = (byte) 0x99;
            final static byte P7_LSB = (byte) 0x9A;
            final static byte P7_MSB = (byte) 0x9B;
            final static byte P8_LSB = (byte) 0x9C;
            final static byte P8_MSB = (byte) 0x9D;
            final static byte P9_LSB = (byte) 0x9E;
            final static byte P9_MSB = (byte) 0x9F;
        }

        static class humidity {
            final static byte H1 = (byte) 0xA1;
            final static byte H2_LSB = (byte) 0xE1;
            final static byte H2_MSB = (byte) 0xE2;
            final static byte H3 = (byte) 0xE3;
            final static byte H4_MSB = (byte) 0xE4;
            final static byte H4_LSB = (byte) 0xE5;
            final static byte H5_MSB = (byte) 0xE6;
            final static byte H6 = (byte) 0xE7;
        }
    }

    private class calibration_param {
        int dig_T1;
        int dig_T2;
        int dig_T3;

        int dig_P1;
        int dig_P2;
        int dig_P3;
        int dig_P4;
        int dig_P5;
        int dig_P6;
        int dig_P7;
        int dig_P8;
        int dig_P9;

        int dig_H1;
        int dig_H2;
        int dig_H3;
        int dig_H4;
        int dig_H5;
        int dig_H6;

        int  t_fine;
    }

    private byte status_reg = 0x0;
    private byte config_reg = 0x0;
    private byte ctrl_meas_reg = 0x0;
    private byte ctrl_hum_reg = 0x0;
    private byte oversamp_humidity = 0x0;
    private byte oversamp_pressure = 0x0;
    private byte oversamp_temperature = 0x0;

    private calibration_param param;

    public BME280(I2cDevice device) {
        this.device = device;
        param = new calibration_param();
    }

    public void init(byte[] argv)
            throws IOException, IllegalAccessException, InterruptedException {
        init(argv[0], argv[1], argv[2], argv[3]);
    }
    public void init(byte powerMode, byte hSamp, byte pSamp, byte tSamp)
            throws IOException, IllegalAccessException, InterruptedException {
        if (device.readRegByte(reg.CHIP_ID) != ID)
            throw new IllegalAccessException("wrong device");
        getCalParam();
        setPowerMode(powerMode);
        setOversampHumidity(hSamp);
        setOversampPressure(pSamp);
        setOversampTemperature(tSamp);
        Thread.sleep(10);
        param.t_fine = 0;
    }

    private void getCalParam() throws IOException {
        byte params[] = new byte[26];
        device.readRegBuffer(cali_reg.temperature.T1_LSB, params, 26);

        param.dig_T1 = ((params[1] & 0xFF) << 8) | (params[0] & 0xFF);
        param.dig_T2 = ((params[3] & 0xFF) << 8) | (params[2] & 0xFF);
        if (param.dig_T2 > 0x7FFF)
            param.dig_T2 -= 0x10000;
        param.dig_T3 = ((params[5] & 0xFF) << 8) | (params[4] & 0xFF);
        if (param.dig_T3 > 0x7FFF)
            param.dig_T3 -= 0x10000;

        param.dig_P1 = ((params[7] & 0xFF) << 8) | (params[6] & 0xFF);
        param.dig_P2 = ((params[9] & 0xFF) << 8) | (params[8] & 0xFF);
        Log.d("wb", "P2 - " + param.dig_P2);

        if (param.dig_P2 > 0x7FFF)
            param.dig_P2 -= 0x10000;
        param.dig_P3 = ((params[11] & 0xFF) << 8) | (params[10] & 0xFF);
        if (param.dig_P3 > 0x7FFF)
            param.dig_P3 -= 0x10000;
        param.dig_P4 = ((params[13] & 0xFF) << 8) | (params[12] & 0xFF);
        if (param.dig_P4 > 0x7FFF)
            param.dig_P4 -= 0x10000;
        param.dig_P5 = ((params[15] & 0xFF) << 8) | (params[14] & 0xFF);
        if (param.dig_P5 > 0x7FFF)
            param.dig_P5 -= 0x10000;
        param.dig_P6 = ((params[17] & 0xFF) << 8) | (params[16] & 0xFF);
        if (param.dig_P6 > 0x7FFF)
            param.dig_P6 -= 0x10000;
        param.dig_P7 = ((params[19] & 0xFF) << 8) | (params[18] & 0xFF);
        if (param.dig_P7 > 0x7FFF)
            param.dig_P7 -= 0x10000;
        param.dig_P8 = ((params[21] & 0xFF) << 8) | (params[20] & 0xFF);
        if (param.dig_P8 > 0x7FFF)
            param.dig_P8 -= 0x10000;
        param.dig_P9 = ((params[23] & 0xFF) << 8) | (params[22] & 0xFF);
        if (param.dig_P9 > 0x7FFF)
            param.dig_P9 -= 0x10000;

        param.dig_H1 = params[25] & 0xFF;

        params = new byte[7];
        device.readRegBuffer(cali_reg.humidity.H2_LSB, params,7);
        param.dig_H2 = (params[1] & 0xFF << 8 | params[0] & 0xFF);
        if (param.dig_H2 > 0x7FFF)
            param.dig_H2 -= 0x10000;
        param.dig_H3 = (params[2] & 0xFF);
        param.dig_H4 = ((params[3] & 0xFF) << 4) | (params[4] & 0x0F);
        if (param.dig_H4 > 0x7FFF)
            param.dig_H4 -= 0x10000;
        param.dig_H5 = ((params[5] & 0xFF) << 4) | ((params[4] & 0xFF) >> 4);
        if (param.dig_H5 > 0x7FFF)
            param.dig_H5 -= 0x10000;
        param.dig_H6 = params[6] & 0xFF;
        if (param.dig_H6 > 127)
            param.dig_H6 -= 256;
    }

    private void softrst() throws IOException {
        device.writeRegByte(reg.RST, POWER_MODE.SOFT_RESET_CODE);
    }

    private byte getPowerMode() throws IOException {
        return (byte) (device.readRegByte(reg.CTRL_MEAS) & 0b11);
    }

    private void setPowerMode (byte powerMode)
            throws IOException, InterruptedException, IllegalArgumentException{
        if (powerMode <=  POWER_MODE.NORMAL) {
            ctrl_meas_reg = (byte) (ctrl_meas_reg & ~0b11 | powerMode & 0b11);
            if (getPowerMode() != POWER_MODE.SLEEP) {
                softrst();
                Thread.sleep(3);
                writeRegs();
            } else
                device.writeRegByte(reg.CTRL_MEAS, ctrl_meas_reg);

            readRegs();
        } else
            throw new IllegalArgumentException("Wrong Power Mode " + powerMode);
    }

    private void setOversampHumidity(byte sampling)
            throws IOException, InterruptedException {
        ctrl_hum_reg = (byte)(ctrl_hum_reg & ~ 0b0111 | sampling & 0b0111);

        if (getPowerMode() != POWER_MODE.SLEEP) {
            softrst();
            Thread.sleep(3);
            writeRegs();
        } else {
            device.writeRegByte(reg.CTRL_HUMIDITY, ctrl_hum_reg);
            device.writeRegByte(reg.CTRL_MEAS, ctrl_hum_reg);
        }

        oversamp_humidity = sampling;
        readRegs();
    }

    private void setOversampPressure(byte sampling)
        throws IOException, InterruptedException {
        ctrl_meas_reg = (byte) (ctrl_meas_reg & ~(0b111 << 2) | ((sampling & 0b111)<<2));

        if (getPowerMode() != POWER_MODE.SLEEP) {
            softrst();
            Thread.sleep(3);
            writeRegs();
        } else
            device.writeRegByte(reg.CTRL_MEAS, ctrl_meas_reg);

        oversamp_pressure = sampling;
        readRegs();
    }

    private void setOversampTemperature(byte sampling)
            throws IOException, InterruptedException {
        ctrl_meas_reg = (byte) (ctrl_meas_reg & ~(0b111 << 5) | ((sampling & 0b111) << 5));

        if (getPowerMode() != POWER_MODE.SLEEP) {
            softrst();
            Thread.sleep(3);
            writeRegs();
        } else
            device.writeRegByte(reg.CTRL_MEAS, ctrl_meas_reg);

        oversamp_temperature = sampling;
        readRegs();
    }

    private void readRegs() throws IOException {
        ctrl_hum_reg = device.readRegByte(reg.CTRL_HUMIDITY);
        ctrl_meas_reg = device.readRegByte(reg.CTRL_MEAS);
        config_reg = device.readRegByte(reg.CONFIG);
    }

    private void writeRegs()
            throws IOException{
        device.writeRegByte(reg.CTRL_HUMIDITY, ctrl_hum_reg);
        device.writeRegByte(reg.CTRL_MEAS, ctrl_meas_reg);
        device.writeRegByte(reg.CONFIG, config_reg);
    }

    public double readHumidity() throws IOException {
        byte[] buffer = new byte[2];
        device.readRegBuffer(reg.humidity.MSB, buffer, 2);
        long adc_h = (((long)(buffer[0] & 0xFF) << 8) + (long)(buffer[1] & 0xFF));

        // Humidity offset calculations
        double var_H = (param.t_fine - 76800.0);
        var_H = (adc_h - (param.dig_H4 * 64.0 + param.dig_H5 / 16384.0 * var_H))
                * (param.dig_H2 / 65536.0 * (1.0 + param.dig_H6 / 67108864.0 * var_H * (1.0 + param.dig_H3 / 67108864.0 * var_H)));
        double humidity = var_H * (1.0 -  param.dig_H1 * var_H / 524288.0);
        if(humidity > 100.0)
        {
            humidity = 100.0;
        }else
        if(humidity < 0.0)
        {
            humidity = 0.0;
        }
        return humidity;
    }

    public double readPressure() throws IOException {
        byte[] buffer = new byte[3];
        device.readRegBuffer(reg.pressure.MSB, buffer, 3);
        long adc_p = (((long)(buffer[0] & 0xFF) << 16) | ((long)(buffer[1] & 0xFF) << 8) | (long)(buffer[2] & 0xF0)) >> 4;

        double var1 = (param.t_fine / 2.0) - 64000.0;
        double var2 = var1 * var1 * ((double)param.dig_P6) / 32768.0;
        var2 = var2 + var1 * ((double)param.dig_P5) * 2.0;
        var2 = (var2 / 4.0) + (((double)param.dig_P4) * 65536.0);
        var1 = (((double) param.dig_P3) * var1 * var1 / 524288.0 + ((double) param.dig_P2) * var1) / 524288.0;
        var1 = (1.0 + var1 / 32768.0) * ((double)param.dig_P1);
        double p = 1048576.0 - (double)adc_p;
        p = (p - (var2 / 4096.0)) * 6250.0 / var1;
        var1 = ((double) param.dig_P9) * p * p / 2147483648.0;
        var2 = p * ((double) param.dig_P8) / 32768.0;
        double pressure = (p + (var1 + var2 + ((double)param.dig_P7)) / 16.0) / 100;

        return pressure;
    }

    public double readTemperature() throws IOException {
        byte[] buffer = new byte[3];
        device.readRegBuffer(reg.temperature.MSB, buffer, 3);
        long adc_t = (((long)(buffer[0] & 0xFF) << 16) | ((long)(buffer[1] & 0xFF) << 8) | (long)(buffer[2] & 0xF0)) >> 4;

        double var1 = (((double)adc_t) / 16384.0 - ((double)param.dig_T1) / 1024.0) * ((double)param.dig_T2);
        double var2 = ((((double)adc_t) / 131072.0 - ((double)param.dig_T1) / 8192.0) *
                (((double)adc_t)/131072.0 - ((double)param.dig_T1)/8192.0)) * ((double)param.dig_T3);
        param.t_fine = (int)(var1 + var2);
        double cTemp = (var1 + var2) / 5120.0;
        double fTemp = cTemp * 1.8 + 32;
        return cTemp;
    }
}
