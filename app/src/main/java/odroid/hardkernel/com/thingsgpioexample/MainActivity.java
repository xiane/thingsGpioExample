package odroid.hardkernel.com.thingsgpioexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;

import odroid.hardkernel.com.max30100.Max30100;
import odroid.hardkernel.com.max30100.PulseOxiMeter;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MAX30100";
    Max30100 sensor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Thread sensorThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    sensor = new Max30100("I2C-1");
//
//                    sensor.initialize();
//
//                    sensor.begin(Max30100.modeControl.SPO2_HR,
//                            Max30100.pulseWidth.us1600,
//                            Max30100.ledCurrent.i50, Max30100.ledCurrent.i27,
//                            Max30100.sampleRate.hz100);
//                    sensor.setHiResModeEnable(true);
//                    sensor.resetFifo();
//
//                    while (true) {
//                        int sampleNum = sensor.update();
//                        while (sampleNum-- > 0) {
//                            Log.d(TAG, "IR - " + sensor.getIR());
//                            Log.d(TAG, "RED - " + sensor.getRED());
//                        }
//                        try {
//                            Thread.sleep(100);
//                        } catch (InterruptedException e) {
//
//                        }
//                    }
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
