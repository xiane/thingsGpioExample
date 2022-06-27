package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.SpiDevice;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    final String TAG = "SpiLoopbackExample";
    PeripheralManager manager;
    SpiDevice spi;

    private TextView spi_rx_txt;
    private EditText spi_tx_txt;
    private Button spi_send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spi_rx_txt = findViewById(R.id.spi_msg_txt);
        spi_tx_txt = findViewById(R.id.spi_send_txt);
        spi_send_btn = findViewById(R.id.spi_send_btn);

        // get Peripheral Manager for managing the gpio.
        manager = PeripheralManager.getInstance();

        // get available spi list.
        List<String> spiList = manager.getSpiBusList();

        try {
            // get SPI bus.
            spi = manager.openSpiDevice(spiList.get(0));

            // set the SPI initial state.
            spi.setFrequency(10000000);
            spi.setMode(SpiDevice.MODE0);
            spi.setBitsPerWord(8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        spi_send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable msg = spi_tx_txt.getText();
                try {
                    byte[] msgByte = msg.toString().getBytes(StandardCharsets.UTF_8);
                    Log.d(TAG, Arrays.toString(msgByte));

                    final byte[] rxByte = new byte[msgByte.length];
                    spi.transfer(msgByte, rxByte, msgByte.length);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                final byte[] buff = rxByte;
                                spi_rx_txt.setText(new String(buff, 0, buff.length, "UTF-8"));
                                spi_tx_txt.setText("");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
