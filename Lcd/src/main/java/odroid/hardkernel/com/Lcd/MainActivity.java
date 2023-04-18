package odroid.hardkernel.com.Lcd;

import android.hardkernel.com.Lcd.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.hardkernel.odroid.things.contrib.Lcd.Lcd;
import com.hardkernel.odroid.things.contrib.Eeprom.at24c32;
import java.io.IOException;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    LCD lcd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            lcd = new LCD("I2C-1");
            lcd.init(20, 4);

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
                            printLcd(bf, 1);
                            sleep(3000);
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            lcdThread.start();

            while (true) {
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
