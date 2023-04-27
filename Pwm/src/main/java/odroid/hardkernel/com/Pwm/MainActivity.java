package odroid.hardkernel.com.Pwm;

import android.hardkernel.com.Pwm.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.SeekBar;
import android.widget.Switch;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get a Peripheral Manager for managing the pwm.
        manager = PeripheralManager.getInstance();

        // get available pwm pin list.
        // each pwm name is physical pin number.
        List<String> pwmList = manager.getPwmList();

        for(String pin:pwmList)
            Log.d("things", "pwm name - " + pin);
        Switch pwmSwitch = findViewById(R.id.pwm_switch);
        try {
            final Pwm pwm = manager.openPwm(pwmList.get(1));

            pwm.setPwmFrequencyHz(3000);
            SeekBar pwm_dutyCycle = findViewById(R.id.pwm_dutyCycle_seekbar);

            pwm_dutyCycle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        pwm.setPwmDutyCycle(progress);
                    } catch (IOException e) {}
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            pwmSwitch.setOnClickListener(v -> {
                try {
                    Switch gpioSwitch = (Switch) v;
                    if (gpioSwitch.isChecked()) {
                        pwm.setEnabled(true);

                    } else {
                        pwm.setEnabled(false);
                    }
                } catch (IOException io) {
                    io.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}