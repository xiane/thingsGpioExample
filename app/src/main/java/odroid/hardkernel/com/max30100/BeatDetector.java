package odroid.hardkernel.com.max30100;

public class BeatDetector {
    enum State {
        INIT, WAITING, FOLLOWING_SLOPE, MAYBE_DETECTED, MASKING
    }

    private static final int INIT_HOLDOFF = 2000;
    private static final int MASKING_HOLDOFF = 200;
    private static final double BPFILTER_ALPHA = 0.6;
    private static final int STEP_RESILIENCY = 30;

    private static final int MIN_THRESHOLD = 20;
    private static final int MAX_THRESHOLD = 800;

    private static final double THRESHOLD_FALLOFF_TARGET = 0.3;

    private static final double THRESHOLD_DECAY_FACTOR = 0.99;

    private static final int INVALID_READOUT_DELAY = 2000;

    private static final int SAMPLES_PERIOD = 10;

    private State state = State.INIT;
    private double threshold = MIN_THRESHOLD;
    private double beatPeriod = 0;
    private double lastMax = 0;
    private long lastBeat = 0;

    public BeatDetector() {}

    public boolean addSample(double sample) {
        return checkForBeat(sample);
    }

    public double getRate() {
        if (beatPeriod != 0)
            return 1 / beatPeriod * 1000 * 60;
        else
            return 0;
    }

    public double getCurrentThreshold() {
        return threshold;
    }

    private boolean checkForBeat(double sample) {
        boolean beatDetected = false;

        switch (state) {
            case INIT:
                if (System.currentTimeMillis() > INIT_HOLDOFF)
                    state = State.WAITING;
                break;
            case WAITING:
                if (sample > threshold) {
                    threshold = Math.min(sample, MAX_THRESHOLD);
                    state = State.FOLLOWING_SLOPE;
                }

                if (System.currentTimeMillis() - lastBeat > INVALID_READOUT_DELAY) {
                    beatPeriod = 0;
                    lastMax = 0;
                }

                decreaseThreshold();
                break;
            case FOLLOWING_SLOPE:
                if (sample < threshold)
                    state = State.MAYBE_DETECTED;
                else
                    threshold = Math.min(sample, MAX_THRESHOLD);
                break;
            case MAYBE_DETECTED:
                if (sample + STEP_RESILIENCY < threshold) {
                    beatDetected = true;
                    lastMax = sample;
                    state = State.MASKING;

                    long delta = System.currentTimeMillis() - lastBeat;
                    if (delta > 0)
                        beatPeriod = BPFILTER_ALPHA * delta + (1 - BPFILTER_ALPHA) * beatPeriod;

                    lastBeat = System.currentTimeMillis();
                } else
                    state = State.FOLLOWING_SLOPE;
                break;
            case MASKING:
                if (System.currentTimeMillis() - lastBeat > MASKING_HOLDOFF)
                    state = State.WAITING;
                decreaseThreshold();
                break;
        }

        return beatDetected;
    }

    private void decreaseThreshold() {
        if (lastMax > 0
            && beatPeriod > 0) {
            threshold -= lastMax * (1 - THRESHOLD_FALLOFF_TARGET)
                    / (beatPeriod / SAMPLES_PERIOD);
        } else
            threshold *= THRESHOLD_DECAY_FACTOR;

        if (threshold < MIN_THRESHOLD)
            threshold = MIN_THRESHOLD;
    }
}
