package odroid.hardkernel.com.max30100;

import java.io.IOException;

public class PulseOxiMeter {

    private static final int CURRENT_PERIOD_MS = 500;

    private Max30100 sensor;

    private DCRemover irRemover;
    private DCRemover redRemover;

    enum State {
        INIT, IDLE, DETECTING
    }

    private State state = State.INIT;

    private BeatDetector beatDetector = new BeatDetector();

    FilterBuLp1 lpf = new FilterBuLp1();

    int redCurrent = Max30100.ledCurrent.i27.ordinal();
    int irCurrent = Max30100.ledCurrent.i50.ordinal();

    SpO2Calculator spO2Calculator = new SpO2Calculator();

    private static final double DC_REMOVER_ALPHA = 0.95;

    public PulseOxiMeter (String i2c) throws IOException {
        sensor = new Max30100(i2c);
    }

    public void initialize() throws IOException {
        sensor.initialize();

        sensor.setMode(Max30100.modeControl.SPO2_HR);
        sensor.setLedCurrent(irCurrent, redCurrent);
        sensor.resetFifo();

        irRemover = new DCRemover(DC_REMOVER_ALPHA);
        redRemover = new DCRemover(DC_REMOVER_ALPHA);

        state = State.IDLE;
    }

    public void update() throws IOException {
        int sampleNum = sensor.update();

        if (sampleNum > 0) {
            checkSample(sampleNum);
            checkCurrentBias();
        }
    }

    public double getHeartRate() {
        return beatDetector.getRate();
    }

    public int getSpO2() {
        return spO2Calculator.getSpO2();
    }

    public int getRedCurrentBias() {
        return redCurrent;
    }

    public void setIRCurrent(Max30100.ledCurrent irNewCurrent) throws IOException {
        irCurrent = irNewCurrent.ordinal();
        sensor.setLedCurrent(irCurrent, redCurrent);
    }

    public void shutdown() throws IOException {
        sensor.shutdown();
    }

    public void resume() throws IOException {
        sensor.resume();
    }

    private void checkSample(int sampleNum) {
        int rawIR, rawRed;

        while (sampleNum-- > 0) {
            rawIR = sensor.getIR();
            rawRed = sensor.getRED();

            double irAC = irRemover.step(rawIR);
            double redAC = redRemover.step(rawRed);

            double filteredPulse = lpf.step(-irAC);
            boolean beatDetected = beatDetector.addSample(filteredPulse);

            if (beatDetector.getRate() > 0) {
                state = State.DETECTING;
                spO2Calculator.update(irAC, redAC, beatDetected);
            } else if (state == State.DETECTING) {
                state = State.IDLE;
                spO2Calculator.reset();
            }
        }
    }

    private long lastBiasCheck = 0;
    private void checkCurrentBias() throws IOException {
        if (System.currentTimeMillis() - lastBiasCheck > CURRENT_PERIOD_MS) {
            boolean changed = false;
            if (irRemover.getDCW() - redRemover.getDCW() > 70000
                && redCurrent < Max30100.ledCurrent.i50.ordinal()) {
                ++redCurrent;
                changed = true;
            } else if (redRemover.getDCW() - irRemover.getDCW() > 70000
                && redCurrent > 0) {
                --redCurrent;
                changed = true;
            }

            if (changed) {
                sensor.setLedCurrent(irCurrent, redCurrent);
            }

            lastBiasCheck = System.currentTimeMillis();
        }
    }

    //http://www.schwietering.com/jayduino/filtuino/
    //Low pass butterworth filter order=1 alpha1=0.1
    //Fs=100Hz, Fc=6Hz
    class FilterBuLp1 {
        private double[] v = new double[2];

        public FilterBuLp1() {
            v[0] = 0.0;
        }

        public double step(double x) // class II
        {
            v[0] = v[1];
            v[1] = (2.452372752527856026e-1 * x) + (0.50952544949442879485 * v[0]);
            return (v[0] + v[1]);
        }
    }

    // http://sam-koblenski.blogspot.de/2015/11/everyday-dsp-for-programmers-dc-and.html
    class DCRemover {
        double alpha;
        double dcw;

        public DCRemover(double initAlpha) {
            alpha = initAlpha;
            dcw = 0;
        }

        public double step(double nextDcw) {
            double oldDcw = dcw;
            dcw = nextDcw + alpha * dcw;

            return dcw - oldDcw;
        }

        public double getDCW() {
            return dcw;
        }
    }
}
