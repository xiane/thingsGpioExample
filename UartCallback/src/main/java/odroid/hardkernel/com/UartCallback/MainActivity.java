package odroid.hardkernel.com.UartCallback;

import android.hardkernel.com.UartCallback.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TAG = "UartExample";
    PeripheralManager manager;
    UartDevice uart;
    UartDeviceCallback callback;

    private Handler uartHandler;

    private TextView uart_rx_txt;
    private EditText uart_tx_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uart_rx_txt = findViewById(R.id.uart_msg_txt);
        uart_tx_txt = findViewById(R.id.uart_send_txt);
        Button uart_send_btn = findViewById(R.id.uart_send_btn);

        uart_send_btn.setOnClickListener(v -> {
            Editable msg = uart_tx_txt.getText();
            try {
                byte[] msgByte = msg.toString().getBytes(StandardCharsets.UTF_8);
                Log.d(TAG, Arrays.toString(msgByte));
                uart.write(msgByte, msgByte.length);
            } catch (IOException e) {
                e.printStackTrace();
            }

            runOnUiThread(() -> uart_tx_txt.setText(""));
        });

        HandlerThread uartBackGround = new HandlerThread("UartBackground");
        uartBackGround.start();
        uartHandler = new Handler(uartBackGround.getLooper());

        // get Peripheral Manager for managing the gpio.
        manager = PeripheralManager.getInstance();

        // get available uart pin list.
        // each uart name is UART-# number.
        List<String> uartList = manager.getUartDeviceList();

        try {
            // get first available a uart.
            // in this case, UART-1 is used.
            uart = manager.openUartDevice(uartList.get(0));

            // baudRate - 115200, 8N1, non hardware flow control
            uart.setBaudrate(115200);
            uart.setDataSize(8);
            uart.setParity(UartDevice.PARITY_NONE);
            uart.setStopBits(1);
            uart.setHardwareFlowControl(UartDevice.HW_FLOW_CONTROL_NONE);

            callback = uartDevice -> {
                final byte[] buff = new byte[20];
                try {
                    int length = uartDevice.read(buff, 20);

                    if (length > 0) {
                        for(int i=0; i<length; i++) {
                            Log.d("UartExample", String.format("[%d] %d\n", i, buff[i]));
                        }
                        final String retString =
                                new String(buff, 0, length, StandardCharsets.UTF_8);
                        Log.d("UartExample", "msg - " + retString);
                        runOnUiThread(() -> uart_rx_txt.setText(retString));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            };

            final SwitchCompat gpio = findViewById(R.id.gpio_switch);

            gpio.setOnClickListener(v -> {
                try {
                    if (gpio.isChecked()) {
                        uart.registerUartDeviceCallback(uartHandler, callback);
                    } else {
                        uart.unregisterUartDeviceCallback(callback);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}