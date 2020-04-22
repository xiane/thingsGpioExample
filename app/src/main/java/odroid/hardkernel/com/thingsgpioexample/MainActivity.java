package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

import odroid.hardkernel.com.Lcd.LCD;

public class MainActivity extends AppCompatActivity {
    LCD lcd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lcd = new LCD("I2C-1");
            lcd.init(20, 4);
            while (true) {
                lcd.print("****ODROID-N2****", 1);
                lcd.print("ODROID-magazine ", 2);

                lcd.print("A speed is reliable?", 3);
                lcd.print("Or is it really slow", 4);
                Thread.sleep(3000);

                lcd.print("***HardKernel***", 1);
                lcd.print("*hardkernel.com*", 2);

                lcd.print("This is I2C test apk", 3);
                lcd.print("4th line is work yeh", 4);
                Thread.sleep(3000);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}