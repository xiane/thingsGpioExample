package odroid.hardkernel.com.WeatherBoard;

import android.annotation.SuppressLint;
import android.hardkernel.com.WeatherBoard.R;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.hardkernel.odroid.things.contrib.WeatherBoard.WeatherBoard;

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
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            try {
                uv.setText(String.format("%10.2f", (board.readUV() / 100.0)));
                visible.setText(String.format(" %3.2f LUX", board.readVisible()));
                ir.setText(String.format("%3.2f LUX", board.readIR()));

                temp.setText(String.format(" %2.2fC", board.readTemperatureC()));
                hum.setText(String.format("%3.2f%%", board.readHumidity()));
                double pressure = board.readPressure();
                press.setText(String.format("%4.2fhPa", pressure));
                altitude.setText(String.format("%3.2fM", board.readAltitude(pressure, 1024.25)));
            } catch(Exception e) {e.printStackTrace();}
        }
    };

    class rapidRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            board = new WeatherBoard(BoardDefaults.getI2CPort());
        } catch (Exception e) {
            e.printStackTrace();
        }

        rapidRunnable rapid = new rapidRunnable();
        Thread thread = new Thread(rapid);

        thread.start();
    }
}