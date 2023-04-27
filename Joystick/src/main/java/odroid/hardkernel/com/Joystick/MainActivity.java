package odroid.hardkernel.com.Joystick;

import androidx.appcompat.app.AppCompatActivity;

import com.hardkernel.odroid.things.contrib.Mcp300x.Mcp3008;
import com.hardkernel.odroid.things.contrib.Mcp300x.Mcp300x;

import android.annotation.SuppressLint;
import android.hardkernel.com.Joystick.R;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Mcp3008 mcp3008;
    private TextView joystick_Values;

    private static final int FRAME_DELAY_MS = 150;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            mcp3008 = new Mcp3008(BoardDefaults.getSPIPort());
        } catch (IOException e) {
            e.printStackTrace();
        }

        HandlerThread mThread = new HandlerThread("Thread");
        mThread.start();
        mHandler = new Handler(mThread.getLooper());
        mHandler.post(mRunnable);
        joystick_Values = findViewById(R.id.joystick_value);
    }

    private final Runnable mRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            try {
                int[] targetChannel = {0, 1, 2};
                int[] adcValues = mcp3008.readADC(targetChannel, Mcp300x.ADC_MODE.SINGLE_END);

                runOnUiThread(() ->
                        joystick_Values.setText(
                                String.format("x - %3d, y - %3d, sw - %3d",
                                        adcValues[0], adcValues[1], adcValues[2])));
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(mRunnable, FRAME_DELAY_MS);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            mcp3008.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}