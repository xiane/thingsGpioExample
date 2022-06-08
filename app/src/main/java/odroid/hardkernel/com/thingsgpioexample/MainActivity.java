package odroid.hardkernel.com.thingsgpioexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;
    UartDevice uart;
    UartDeviceCallback callback;

    private HandlerThread uartBackGround;
    private Handler uartHandelr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uartBackGround = new HandlerThread("UartBackground");
        uartBackGround.start();
        uartHandelr = new Handler(uartBackGround.getLooper());

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

            callback = new UartDeviceCallback() {
                @Override
                public boolean onUartDeviceDataAvailable(UartDevice uartDevice) {
                    byte[] buff = new byte[20];
                    try {
                        int length = uartDevice.read(buff, 20);

                        if (length > 0) {
                            String retString = new String(buff, 0, length);
                            Log.d("UartExample", "msg - " + retString);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            };

            final Switch gpio = findViewById(R.id.gpio_switch);

            gpio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (gpio.isChecked()) {
                            uart.registerUartDeviceCallback(uartHandelr, callback);
                        } else {
                            uart.unregisterUartDeviceCallback(callback);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
