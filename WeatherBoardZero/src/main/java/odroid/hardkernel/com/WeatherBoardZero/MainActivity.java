package odroid.hardkernel.com.WeatherBoardZero;

import android.annotation.SuppressLint;
import android.hardkernel.com.WeatherBoardZero.R;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;

import com.hardkernel.odroid.things.contrib.Shtc1.Shtc3;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView temp;
    TextView hum;
    TextView chipId;

    Shtc3 shtc1;

    final Handler handler = new Handler(Looper.getMainLooper());
    final Runnable weather = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            try {
                temp.setText(String.format(" %2.2fC", shtc1.readTemperature()));
                hum.setText(String.format("%2.2f%%", shtc1.readHumidity()));
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

        chipId = findViewById(R.id.chip_id);
        temp = findViewById(R.id.text_temperature);
        hum = findViewById(R.id.text_humidity);

        try {
            shtc1 = new Shtc3(BoardDefaults.getI2CPort());
            chipId.setText(String.format("0x%x", shtc1.getId()));
            shtc1.setPrecision(Shtc3.Precision.High);

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (shtc1.isCorrectId()) {
                rapidRunnable rapid = new rapidRunnable();
                Thread thread = new Thread(rapid);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}