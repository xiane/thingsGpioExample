package odroid.hardkernel.com.RotaryEncoderNServoMotor;

import android.hardkernel.com.RotaryEncoderNServoMotor.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import com.google.android.things.contrib.driver.pwmservo.Servo;
import com.hardkernel.odroid.things.contrib.RotaryEncoder.IncrementalRotaryEncoder;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.GpioCallback;

public class MainActivity extends AppCompatActivity {
    private final int MIN = -90;
    private final int MAX = 90;

    private Servo mServo;
    private IncrementalRotaryEncoder rotaryEncoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mServo = new Servo("12");
            mServo.setPulseDurationRange(0.75, 2.6);
            mServo.setAngleRange(MIN, MAX);
            mServo.setEnabled(true);

            mServo.setAngle(40);

            rotaryEncoder = new IncrementalRotaryEncoder("13", "11", "7",
                    new IncrementalRotaryEncoder.RotaryListener() {
                int duty = 0;
                @Override
                public void cw() {
                    duty -= 1;
                    if (duty < MIN)
                        duty = MIN;
                    try {
                        Log.d("cw", "duty - " + duty);
                        mServo.setAngle(duty);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void ccw() {
                    duty += 1;
                    if (duty > MAX)
                        duty = MAX;
                    try {
                        Log.d("ccw", "duty - " + duty);
                        mServo.setAngle(duty);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            rotaryEncoder.registerSwitch(new GpioCallback() {
                @Override
                public boolean onGpioEdge(Gpio gpio) {
                    Log.d("sw", "click!");
                    return true;
                }
            });

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        rotaryEncoder.startEncoder();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}