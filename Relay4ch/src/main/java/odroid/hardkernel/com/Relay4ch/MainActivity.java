package odroid.hardkernel.com.Relay4ch;

import android.content.Context;
import android.hardkernel.com.Relay4ch.R;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static PeripheralManager manager;
    static class RelaySwitch extends SwitchCompat {
        private Gpio targetRelay = null;
        public RelaySwitch(@NonNull Context context, String name) {
            super(context);
            try {
                targetRelay = manager.openGpio(name);
                targetRelay.setDirection(Gpio.DIRECTION_OUT_INITIALLY_HIGH);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            setScaleX(2.5F);
            setScaleY(2.5F);
        }

        @Override
        public void setChecked(boolean checked) {
            super.setChecked(checked);
            try {
                if (targetRelay != null)
                    targetRelay.setValue(!checked);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = PeripheralManager.getInstance();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Draw();
    }

    public void Draw() {
        LinearLayout odd = findViewById(R.id.odd_layout);
        LinearLayout even = findViewById(R.id.even_layout);

        String[] RelayGpios = BoardDefaults.getRelayChGpios();
        List<String> availableGpioList = manager.getGpioList();

        for (int i =0; i < RelayGpios.length; i++) {
            if (!availableGpioList.contains(RelayGpios[i]))
                continue;
            final RelaySwitch relaySwitch = new RelaySwitch(this, RelayGpios[i]);
            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(60, 20, 40, 20);
            relaySwitch.setLayoutParams(layoutParams);
            relaySwitch.setText("" + i);

            if (i % 2 == 1) {
                even.addView(relaySwitch);
            } else {
                odd.addView(relaySwitch);
            }
        }
    }
}
