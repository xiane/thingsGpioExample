package odroid.hardkernel.com.thingsgpioexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import odroid.hardkernel.com.Lcd.LCD;

public class MainActivity extends AppCompatActivity {
    LCD lcd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lcd = new LCD(20, 4);
            lcd.init();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}