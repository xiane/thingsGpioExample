package odroid.hardkernel.com.thingsgpioexample;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import odroid.hardkernel.com.WeatherBoard.WeatherBoard;

public class MainActivity extends AppCompatActivity {
    TextView uv;
    TextView visible;
    TextView ir;

    TextView temp;
    TextView hum;
    TextView press;
    TextView altitude;
    WeatherBoard board;

    final Handler handler = new Handler();
    final Runnable weather = new Runnable() {
        @Override
        public void run() {
            try {
                uv.setText("" + (board.readUV() / 100.0));
                visible.setText(" " + board.readVisible() + " LUX");
                ir.setText(board.readIR() + " LUX");

                temp.setText("" + board.readTemperature() + "C");
                hum.setText("" + board.readHumidity() + "%");
                double pressure = board.readPressure();
                press.setText("" + pressure + "hPa");
                altitude.setText("" + board.readAltitude(pressure, 1024.25) + "M");
            } catch(Exception e) {e.printStackTrace();}
        }
    };

    class rapidRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
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

        temp = findViewById(R.id.text_temperature);
        hum = findViewById(R.id.text_humidity);
        press = findViewById(R.id.text_pressure);
        altitude = findViewById(R.id.text_altitude);

        try {
            board = new WeatherBoard("I2C-2");
            board.init();
        } catch (Exception e) {}

        rapidRunnable rapid = new rapidRunnable();
        Thread thread = new Thread(rapid);

        thread.start();
    }
}