package odroid.hardkernel.com.thingsgpioexample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.things.pio.PeripheralManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;

    TextView uv;
    TextView visible;
    TextView ir;
    WeatherBoard board;

    final Handler handler = new Handler();
    final Runnable weather = new Runnable() {
        @Override
        public void run() {
            try {
                uv.setText("" + (board.readUV() / 100.0));
                visible.setText(" " + board.readVisible() + " LUX");
                ir.setText(board.readIR() + " LUX");
            } catch(Exception e) {e.printStackTrace();}
        }
    };

    class rapidRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (Exception e) {}
                handler.post(weather);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uv = findViewById(R.id.text_uv);
        visible = findViewById(R.id.text_visiable);
        ir = findViewById(R.id.text_ir);

        // get Peripheral Manager for managing the i2c.
        manager = PeripheralManager.getInstance();

        // get available i2c pin list.
        // each pin name is consist as P + physical pin number.
        List<String> i2cList = manager.getI2cBusList();

        try {
            board = new WeatherBoard(manager, i2cList.get(0));
            board.init();
        } catch (Exception e) {}

        rapidRunnable rapid = new rapidRunnable();
        Thread thread = new Thread(rapid);

        thread.start();
    }
}