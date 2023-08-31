package odroid.hardkernel.com.RotaryEncoderNServoMotor;

import android.hardkernel.com.RotaryEncoderNServoMotor.R;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.io.IOException;
import com.google.android.things.contrib.driver.pwmservo.Servo;
import com.hardkernel.odroid.things.contrib.RotaryEncoder.IncrementalRotaryEncoder;

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
            mServo = new Servo(BoardDefaults.getServoMotorPwm());
            mServo.setPulseDurationRange(0.75, 2.6);
            mServo.setAngleRange(MIN, MAX);
            mServo.setEnabled(true);

            mServo.setAngle(40);

            rotaryEncoder = new IncrementalRotaryEncoder(
                    BoardDefaults.getDtPin(),
                    BoardDefaults.getSwPin(),
                    BoardDefaults.getClkPin(),
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
            rotaryEncoder.registerSwitch(gpio -> {
                Log.d("sw", "click!");
                return true;
            });

            Thread thread = new Thread(() -> {
                try {
                    rotaryEncoder.startEncoder();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}