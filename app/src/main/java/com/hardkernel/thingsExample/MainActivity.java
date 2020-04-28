package com.hardkernel.thingsExample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.hardkernel.drivers.led.LED;

import java.io.IOException;

public class MainActivity extends Activity {
    private boolean ledOn = false;
    private LED led;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            led = new LED("7");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Switch LED = findViewById(R.id.led_switch);
        LED.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ledOn = !ledOn;
                try {
                    led.turn(ledOn);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
