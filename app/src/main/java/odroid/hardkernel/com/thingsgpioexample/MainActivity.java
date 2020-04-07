package odroid.hardkernel.com.thingsgpioexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;
    UartDevice uart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get Peripheral Manager for managing the gpio.
        manager = PeripheralManager.getInstance();

        // get available uart pin list.
        // each uart name is UART-# number.
        List<String> uartList = manager.getUartDeviceList();

        try {
            // get first available a uart.
            // in this case, UART-1 is used.
            uart = manager.openUartDevice(uartList.get(0));

            // baudrate - 115200, 8N1, non hardware flow control
            uart.setBaudrate(115200);
            uart.setDataSize(8);
            uart.setParity(UartDevice.PARITY_NONE);
            uart.setStopBits(1);
            uart.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE);

            String data = "This is test transfer data";
            uart.write(data.getBytes(), data.length());

            uart.flush(UartDevice.FLUSH_OUT);
            while (true) {
                byte[] buff = new byte[20];

                int length = uart.read(buff, 20);
                if (length > 0) {
                    String retString = new String(buff, 0, length);
                    Log.d("UartExample", "msg - " + retString);
                    if (retString.startsWith("exit"))
                        break;
                }
            }
            uart.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
