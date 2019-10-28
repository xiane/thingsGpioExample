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

        manager = PeripheralManager.getInstance();

        List<String> gpioList = manager.getGpioList();

        try {
            gpio = manager.openGpio(gpioList.get(0));
            gpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
            Switch gpioSwitch = findViewById(R.id.gpio_switch);

            gpioSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Switch gpioSwitch = (Switch) v;
                        if (gpioSwitch.isChecked()) {
                            gpio.setValue(true);
                        } else {
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
