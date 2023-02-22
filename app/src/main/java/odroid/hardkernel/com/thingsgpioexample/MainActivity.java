package odroid.hardkernel.com.thingsgpioexample;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import odroid.hardkernel.com.Lcd.LCD;
import odroid.hardkernel.com.eeprom.at24c;
import odroid.hardkernel.com.eeprom.at24c32;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;
import com.google.android.things.pio.I2cDevice;
import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    LCD lcd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lcd = new LCD("I2C-1");
            lcd.init(20, 4);

            final TextView gpioView[] = new TextView[4];
            gpioView[0] = findViewById(R.id.gpioText1);
            gpioView[1] = findViewById(R.id.gpioText2);
            gpioView[2] = findViewById(R.id.gpioText3);
            gpioView[3] = findViewById(R.id.gpioText4);

            PeripheralManager manager = PeripheralManager.getInstance();

            Gpio gpio[] = new Gpio[4];
            gpio[0] = manager.openGpio("7");
            gpio[1] = manager.openGpio("11");
            gpio[2] = manager.openGpio("13");
            gpio[3] = manager.openGpio("16");

            for(int i=0; i<4; i++){
                gpio[i].setEdgeTriggerType(Gpio.EDGE_FALLING);
                final int finalI = i;
                gpio[i].registerGpioCallback(new GpioCallback() {
                    int val = 0;
                    int idx = finalI;
                    @Override
                    public boolean onGpioEdge(Gpio gpio) {
                        gpioView[idx].setText("[" + idx + "] this is it " + val++);
                        return true;
                    }
                });
            }

            final EditText textInput = findViewById(R.id.TextInputForLCD);
            Button updateLcdBtn = findViewById(R.id.UpdateLCD);
            final at24c32 eeprom= new at24c32("I2C-1", 0x57);

            updateLcdBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = textInput.getText().toString();
                    byte[] data = input.getBytes();
                    try {
                        eeprom.write(0x0, data, data.length);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread lcdThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        at24c32 eeprom = new at24c32("I2C-1", 0x57);

                        while (true) {
                            byte[] bf = eeprom.read(0x0, 80);
                            printLcd(bf, 0);
                            sleep(3000);
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            lcdThread.start();

//            while (true) {
//                lcd.print("****ODROID-N2****", 1);
//                lcd.print("ODROID-magazine ", 2);
//
//                lcd.print("A speed is reliable?", 3);
//                lcd.print("Or is it really slow", 4);
//                Thread.sleep(3000);
//
//                lcd.print("***HardKernel***", 1);
//                lcd.print("*hardkernel.com*", 2);
//
//                lcd.print("This is I2C test apk", 3);
//                lcd.print("4th line is work yeh", 4);
//                Thread.sleep(3000);
//            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void printLcd(byte[] lcd_buffer, int idx) throws IOException {
        final int line_max = 20;
        for (int i=0; i<4; i++)
            printLcdLine(lcd_buffer, idx + (i * line_max), i+1);
    }

    private void printLcdLine(byte[] buf, int idx, int line) throws IOException {
        byte[] lcd_buffer = new byte[20];
        System.arraycopy(buf, idx, lcd_buffer, 0, 20);
        lcd.print(new String(lcd_buffer), line);
    }
}