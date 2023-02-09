package odroid.hardkernel.com.eeprom;

import java.io.IOException;

public class at24c32 extends at24c {
    public at24c32(String i2cBus, int address)
            throws IOException, IllegalArgumentException {
        super(i2cBus, address, 32768/8);
        wr_buffer_size = 32;
        wait_time = 25;

        if ((address & (A_PREFIX | A111)) == 0) {
            throw new IllegalArgumentException("Wrong address");
        }
    }

    @Override
    protected byte[] _read(int offset, int size)
        throws InterruptedException, IOException {
        return super._read(addr_16 | offset, size);
    }

    @Override
    protected void _write(int offset, byte[] val, int size)
            throws InterruptedException, IOException {
        super._write(addr_16 | offset, val, size);
    }
}