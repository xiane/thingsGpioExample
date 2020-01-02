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
            lcd = new LCD();
            lcd.init();
            while (true) {
                lcd.print("***ODROID-N2*** ", LCD.LINE_1);
                lcd.print("ODROID-magazine ", LCD.LINE_2);

                lcd.print("A speed is reliable?", LCD.LINE_3);
                lcd.print("Or is it really slow", LCD.LINE_4);
                Thread.sleep(3000);

                lcd.print("***HardKernel***", LCD.LINE_1);
                lcd.print("*hardkernel.com*", LCD.LINE_2);

                lcd.print("This is I2C test apk", LCD.LINE_3);
                lcd.print("4th line is work yeh", LCD.LINE_4);
                Thread.sleep(3000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}