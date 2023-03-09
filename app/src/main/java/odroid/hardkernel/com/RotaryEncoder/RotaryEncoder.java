package odroid.hardkernel.com.RotaryEncoder;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManager;

import java.io.IOException;

public class RotaryEncoder {
    private RotaryDoing doing;
    private Gpio dt, sw, clk;

    public RotaryEncoder(String dtName, String swName, String clkName,
                         RotaryDoing rotaryDoing) throws IOException, IllegalArgumentException  {
        doing = rotaryDoing;

        PeripheralManager manager = PeripheralManager.getInstance();

        dt = manager.openGpio(dtName);
        sw = manager.openGpio(swName);
        clk = manager.openGpio(clkName);

        dt.setDirection(Gpio.DIRECTION_IN);
        sw.setDirection(Gpio.DIRECTION_IN);
        clk.setDirection(Gpio.DIRECTION_IN);
    }

    public void doLoop() throws IOException {
        boolean cur_clk, cur_dt;
        boolean prev_clk = false;
        boolean prev_dt = false;

        while (true) {
            cur_clk = clk.getValue();
            cur_dt = dt.getValue();

            if (cur_clk && cur_dt) {
                if (!prev_clk && prev_dt)
                    doing.cw();
                else if (prev_clk && !prev_dt)
                    doing.ccw();
            }

            prev_clk = cur_clk;
            prev_dt = cur_dt;
        }
    }
}
