package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.Pwm;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;
    Gpio gpio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get Peripheral Manager for managing the gpio.
        manager = PeripheralManager.getInstance();

        // get available gpio pin list.
        // each pin name is consist as P + physical pin number.
        List<String> gpioList = manager.getPwmList();

        for(String pin:gpioList)
            Log.d("things", "pin name - " + pin);
        Switch gpioSwitch = findViewById(R.id.gpio_switch);
        try {
            final Pwm pwm = manager.openPwm(gpioList.get(1));

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
            gpioSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
