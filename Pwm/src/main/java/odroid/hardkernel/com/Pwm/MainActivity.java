package odroid.hardkernel.com.Pwm;

import android.hardkernel.com.Pwm.R;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.things.pio.PeripheralManager;
import com.google.android.things.pio.Pwm;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    PeripheralManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout pwmListLayout = findViewById(R.id.pwm_list);
        // get a Peripheral Manager for managing the pwm.
        manager = PeripheralManager.getInstance();

        // get available pwm pin list.
        // each pwm name is physical pin number.
        List<String> pwmList = manager.getPwmList();

        for(String pin:pwmList) {
            Log.d("things", "pwm name - " + pin);
            try {
                Pwm pwm = manager.openPwm(pin);
                pwm.setPwmFrequencyHz(3000);
                LinearLayout pwmLayout = buildPwmLayout(pwm);
                pwmListLayout.addView(pwmLayout);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private LinearLayout buildPwmLayout(Pwm pwm) {
        LinearLayout pwmLinear = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = 20;
        layoutParams.setMargins(margin, margin, margin, margin);

        pwmLinear.setLayoutParams(layoutParams);

        pwmLinear.setOrientation(LinearLayout.HORIZONTAL);

        SwitchCompat pwmSwitch = new SwitchCompat(this);
        pwmSwitch.setText(pwm.getName());
        pwmSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                pwm.setEnabled(b);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        pwmLinear.addView(pwmSwitch);

        SeekBar pwmDutyCycle = new SeekBar(this);
        pwmDutyCycle.setLayoutParams(new ViewGroup.LayoutParams(600,50));
        pwmDutyCycle.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                try {
                    pwm.setPwmDutyCycle(i);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        pwmLinear.addView(pwmDutyCycle);

        return pwmLinear;
    }
}