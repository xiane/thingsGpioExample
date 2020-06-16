package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Gpio;

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
        List<String> gpioList = manager.getGpioList();

        try {
            // get first available gpio pin.
            // in this case, Physical pin #7 is used.
            gpio = manager.openGpio(gpioList.get(0));

            // set the pin's direction and initial state.
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Switch gpioSwitch = findViewById(R.id.gpio_switch);

            gpioSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Switch gpioSwitch = (Switch) v;
                        if (gpioSwitch.isChecked()) {
                            // set pin #7 to high, or 1.
                            gpio.setValue(true);
                        } else {
                            // set pin #7 to low, or 0.
                            gpio.setValue(false);
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
