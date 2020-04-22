package odroid.hardkernel.com.thingsgpioexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import odroid.hardkernel.com.max30100.PulseOxiMeter;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MAX30100";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Thread sensorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PulseOxiMeter sensor = new PulseOxiMeter("I2C-1");

                    sensor.initialize();

                    while (true) {
                        sensor.update();

                        double heartRate = sensor.getHeartRate();
                        if (heartRate > 1)
                            Log.d(TAG, "Heart - " + heartRate);

                        double spO2 = sensor.getSpO2();
                        if (spO2 > 0)
                            Log.d(TAG, "spO2 - " + spO2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        sensorThread.run();
    }
}
