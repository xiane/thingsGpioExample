package odroid.hardkernel.com.thingsgpioexample;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.UartDevice;
import com.google.android.things.pio.UartDeviceCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "UartExample";
    PeripheralManager manager;
    UartDevice uart;
    UartDeviceCallback callback;

    private HandlerThread uartBackGround;
    private Handler uartHandelr;

    private TextView uart_rx_txt;
    private EditText uart_tx_txt;
    private Button uart_send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uart_rx_txt = findViewById(R.id.uart_msg_txt);
        uart_tx_txt = findViewById(R.id.uart_send_txt);
        uart_send_btn = findViewById(R.id.uart_send_btn);

        uart_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable msg = uart_tx_txt.getText();
                try {
                    byte[] msgByte = msg.toString().getBytes("UTF-8");
                    Log.d(TAG, Arrays.toString(msgByte));
                    uart.write(msgByte, msgByte.length);

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uart_tx_txt.setText("");
                    }
                });
            }
        });

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
                    final byte[] buff = new byte[20];
                    try {
                        int length = uartDevice.read(buff, 20);

                        if (length > 0) {
                            for(int i=0; i<length; i++) {
                                Log.d("UartExample", String.format("[%d] %d\n", i, buff[i]));
                            }
                            final String retString = new String(buff, 0, length, "UTF-8");
                            Log.d("UartExample", "msg - " + retString);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    uart_rx_txt.setText(retString);
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
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
