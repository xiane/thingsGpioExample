package odroid.hardkernel.com.Led;

import android.content.Context;
import android.hardkernel.com.Led.R;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.things.pio.PeripheralManager;
import com.hardkernel.odroid.things.contrib.Led.Led;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static class LedSwitch extends SwitchCompat {
        private Led targetLed = null;
        public LedSwitch(@NonNull Context context, String name) {
            super(context);

            try {
                targetLed = new Led(name);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        @Override
        public void setChecked(boolean checked) {
            super.setChecked(checked);
            try {
                if (targetLed != null)
                    targetLed.turn(checked);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Draw();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Draw();
    }

    public void Draw() {
        LinearLayout odd = findViewById(R.id.odd_layout);
        LinearLayout even = findViewById(R.id.even_layout);

        PeripheralManager manager = PeripheralManager.getInstance();

        List<String> gpioList = manager.getGpioList();

        for(String gpio: gpioList) {
            final LedSwitch ledSwitch = new LedSwitch(this, gpio);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(8, 8, 8, 8);
            ledSwitch.setLayoutParams(layoutParams);
            ledSwitch.setText(gpio);

            if (Integer.parseInt(gpio) % 2 == 0) {
                even.addView(ledSwitch);
            } else {
                odd.addView(ledSwitch);
            }
        }
    }
}