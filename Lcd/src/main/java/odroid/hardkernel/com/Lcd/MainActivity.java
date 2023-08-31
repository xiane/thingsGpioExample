package odroid.hardkernel.com.Lcd;

import android.hardkernel.com.Lcd.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.widget.SwitchCompat;

import com.hardkernel.odroid.things.contrib.Lcd.Lcd;
import com.hardkernel.odroid.things.contrib.Eeprom.at24c32;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    Lcd lcd;
    final at24c32 eeprom;

    {
        try {
            eeprom = new at24c32(BoardDefaults.getI2CPort(),
                    BoardDefaults.getAddressGpio(), 0x57);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    abstract static class flagRunnable implements Runnable {
        private final AtomicBoolean runFlag = new AtomicBoolean(false);
        public void stop() {
            runFlag.set(false);
        }

        @Override
        public void run() {
            runFlag.set(true);
            preRun();
            while (runFlag.get()) {
                realRun();
            }
            postRun();
        }
        public abstract void preRun();
        public abstract void realRun();
        public abstract void postRun();
    }

    private final flagRunnable eepromRunnable = new flagRunnable() {
        @Override
        public void preRun() {
        }

        @Override
        public void realRun() {
            try {
                byte[] bf = eeprom.read(0x0, 80);
                printLcd(bf, 0);
                sleep(3000);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void postRun() {
            try {
                eeprom.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    };

    private final flagRunnable lcdSelfRunnable = new flagRunnable() {
        @Override
        public void preRun() {}

        @Override
        public void realRun() {
            try {
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
            } catch(IOException| InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void postRun() {}
    };
    private Thread eepromLcdThread;
    private Thread lcdSelfThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText textInput = findViewById(R.id.TextInputForLCD);
        final Button updateLcdBtn = findViewById(R.id.UpdateLCD);
        final SwitchCompat eepromSwitch = findViewById(R.id.eepromSw);

        textInput.setEnabled(false);
        updateLcdBtn.setEnabled(false);

        try {
            lcd = new Lcd(BoardDefaults.getI2CPort(), 20, 4);

            updateLcdBtn.setOnClickListener(v -> {
                String input = textInput.getText().toString();
                byte[] data = input.getBytes();
                try {
                    eeprom.write(0x0, data, data.length);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException|InterruptedException e) {
            e.printStackTrace();
        }

        lcdSelfThread = new Thread(lcdSelfRunnable);
        lcdSelfThread.start();
        eepromSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            textInput.setEnabled(isChecked);
            updateLcdBtn.setEnabled(isChecked);

            try {
                if (isChecked) {
                    lcdSelfRunnable.stop();
                    lcdSelfThread.join();
                    lcdSelfThread = null;

                    eepromLcdThread = new Thread(eepromRunnable);
                    eepromLcdThread.start();
                } else {
                    eepromRunnable.stop();
                    eepromLcdThread.join();
                    eepromLcdThread = null;

                    lcdSelfThread = new Thread(lcdSelfRunnable);
                    lcdSelfThread.start();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
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