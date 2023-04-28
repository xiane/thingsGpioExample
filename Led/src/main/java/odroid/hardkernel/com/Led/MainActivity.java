package odroid.hardkernel.com.Led;

import android.hardkernel.com.Led.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.hardkernel.odroid.things.contrib.Led.Led;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private boolean ledOn = false;
    private Led led;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            led = new Led("7");
        } catch (IOException e) {
            e.printStackTrace();
        }

        SwitchCompat LED = findViewById(R.id.led_switch);
        LED.setOnClickListener(v -> {
            ledOn = !ledOn;
            try {
                led.turn(ledOn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}