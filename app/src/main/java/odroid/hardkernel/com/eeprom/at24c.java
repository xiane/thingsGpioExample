package odroid.hardkernel.com.eeprom;

import static java.lang.Thread.sleep;

import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class at24c {
    protected long wait_time;
    private int byte_length;
    protected byte wr_buffer_size;

    protected I2cDevice i2c;

    public boolean read_only;

    protected final int A_PREFIX = 0x1010000;

    protected final int A000 = 0x000;
    protected final int A001 = 0x001;
    protected final int A010 = 0x010;
    protected final int A011 = 0x011;
    protected final int A100 = 0x100;
    protected final int A101 = 0x101;
    protected final int A110 = 0x110;
    protected final int A111 = 0x111;

    public final int addr_16 = 0x10000000;

    public at24c(String i2cBus, int bus_address, int size)
            throws IOException {
        // get Peripheral Manager for managing the i2c device.
        PeripheralManager manager = PeripheralManager.getInstance();

        /*
          get available i2c pin list.
          i2c name format - I2C-#, and n2/c4 have I2C-1 and I2C-2.
          In this case use given bus. if given bus is not in list, use default one.
         */
        List<String> i2cBuslist = manager.getI2cBusList();
        if(i2cBuslist.contains(i2cBus))
            i2c = manager.openI2cDevice(i2cBus, bus_address);
        else
            i2c = manager.openI2cDevice(i2cBuslist.get(0), bus_address);
        byte_length = size;
        read_only = false;
    }

    public byte[] read(int offset, int size)
            throws IOException, IllegalArgumentException,
            IndexOutOfBoundsException, InterruptedException {
        if (size == 0)
            throw new IllegalArgumentException("size must bigger then zero");
        if (offset + size > byte_length)
            throw new IndexOutOfBoundsException("Denied access to out of size");

        return _read(offset, size);
    }

    protected byte[] _read(int offset, int size)
            throws IOException, InterruptedException {
        byte [] buffer = new byte[size];
        i2c.readRegBuffer(offset, buffer, size);
        sleep(wait_time);
        return buffer;
    }

    public void write (int  offset, byte[] val, int size)
            throws IOException, IllegalArgumentException,
            IndexOutOfBoundsException, InterruptedException,
            UnsupportedOperationException {
        if (read_only)
            throw new UnsupportedOperationException("Read Only");
        if (size == 0)
            throw new IllegalArgumentException("size must bigger then zero");
        if (offset + size > byte_length)
            throw new IndexOutOfBoundsException("Denied access to out of size");

        int idx = 0;
        int remain = size;
        while (remain >= wr_buffer_size) {
            _write(offset + idx,
                    Arrays.copyOfRange(val, idx, idx + wr_buffer_size),
                     wr_buffer_size);
            idx += wr_buffer_size;
            remain -= wr_buffer_size;
        }
        if (remain > 0)
            _write(offset + idx,
                    Arrays.copyOfRange(val, idx, idx + remain),
                    remain);
    }

    protected void _write(int offset, byte[] val, int size)
            throws InterruptedException, IOException {
        i2c.writeRegBuffer(offset, val, size);
        sleep(wait_time);
    }
}
