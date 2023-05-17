package odroid.hardkernel.com.OledNRtc;

import static android.graphics.Bitmap.Config.ARGB_8888;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardkernel.com.OledNRtc.R;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.hardkernel.odroid.things.contrib.Ssd1306.BitmapHelper;
import com.hardkernel.odroid.things.contrib.Ssd1306.Ssd1306;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "OledScreenActivity";
    private static final int FPS = 30; // Frames per second on draw thread
    private static final int BITMAP_FRAMES_PER_MOVE = 4; // Frames to show bitmap before moving it
    private boolean mExpandingPixels = true;
    private int mDotMod = 1;
    private int mBitmapMod = 0;
    private int mTick = 0;
    private Modes mMode = Modes.TIMER;

    Ssd1306 mScreen;

    private final Handler mHandler = new Handler();
    private Bitmap mBitmap;

    enum Modes {
        CROSS_HAIRS,
        DOTS,
        TIMER,
        BITMAP,
        ALL_WHITE,
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mScreen = new Ssd1306(BoardDefaults.getI2CPort(), Ssd1306.I2C_ADDRESS_SA0_LOW);
        } catch (IOException e) {
            Log.e(TAG, "Error while opening screen", e);
            throw new RuntimeException(e);
        }
        Log.d(TAG, "OLED screen activity created");

        RadioGroup oledGroup = findViewById(R.id.oledGroup);

        oledGroup.check(R.id.timer);

        oledGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            switch (id) {
                case R.id.crossHairs:
                    mMode = Modes.CROSS_HAIRS;
                    break;
                case R.id.dots:
                    mMode = Modes.DOTS;
                    break;
                case R.id.timer:
                    mMode = Modes.TIMER;
                    break;
                case R.id.allWhite:
                    mMode = Modes.ALL_WHITE;
                    break;
            }
        });

        Switch display_flip = findViewById(R.id.display_flip);
        Switch display_mirror = findViewById(R.id.display_mirror);
        Switch display_inverse = findViewById(R.id.display_inverse);

        display_flip.setOnCheckedChangeListener((compoundButton, on) -> {
            try {
                mScreen.setDisplayFlip(on);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        display_mirror.setOnCheckedChangeListener((compoundButton, on) -> {
            try {
                mScreen.setDisplayMirror(on);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        display_inverse.setOnCheckedChangeListener((compoundButton, on) -> {
            try {
                mScreen.setDisplayInverse(on);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        mHandler.post(mDrawRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // remove pending runnable from the handler
        mHandler.removeCallbacks(mDrawRunnable);
        // Close the device.
        try {
            mScreen.close();
        } catch (IOException e) {
            Log.e(TAG, "Error closing SSD1306", e);
        } finally {
            mScreen = null;
        }
    }

    /**
     * Draws crosshair pattern.
     */
    private void drawCrosshairs() {
        mScreen.clearPixels();
        int y = mTick % mScreen.getLcdHeight();
        for (int x = 0; x < mScreen.getLcdWidth(); x++) {
            mScreen.setPixel(x, y, true);
            mScreen.setPixel(x, mScreen.getLcdHeight() - (y + 1), true);
        }
        int x = mTick % mScreen.getLcdWidth();
        for (y = 0; y < mScreen.getLcdHeight(); y++) {
            mScreen.setPixel(x, y, true);
            mScreen.setPixel(mScreen.getLcdWidth() - (x + 1), y, true);
        }
    }

    /**
     * Draws expanding and contracting pixels.
     */
    private void drawExpandingDots() {
        if (mExpandingPixels) {
            for (int x = 0; x < mScreen.getLcdWidth(); x++) {
                for (int y = 0; y < mScreen.getLcdHeight() && mMode == Modes.DOTS; y++) {
                    mScreen.setPixel(x, y, (x % mDotMod) == 1 && (y % mDotMod) == 1);
                }
            }
            mDotMod++;
            if (mDotMod > mScreen.getLcdHeight()) {
                mExpandingPixels = false;
                mDotMod = mScreen.getLcdHeight();
            }
        } else {
            for (int x = 0; x < mScreen.getLcdWidth(); x++) {
                for (int y = 0; y < mScreen.getLcdHeight() && mMode == Modes.DOTS; y++) {
                    mScreen.setPixel(x, y, (x % mDotMod) == 1 && (y % mDotMod) == 1);
                }
            }
            mDotMod--;
            if (mDotMod < 1) {
                mExpandingPixels = true;
                mDotMod = 1;
            }
        }
    }

    /**
     * Draws a BMP in one of three positions.
     */
    private void drawMovingBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.flower);
        }
        // Move the bmp every few ticks
        if (mTick % BITMAP_FRAMES_PER_MOVE == 0) {
            mScreen.clearPixels();
            // Move the bitmap back and forth based on mBitmapMod:
            // 0 - left aligned
            // 1 - centered
            // 2 - right aligned
            // 3 - centered
            int diff = mScreen.getLcdWidth() - mBitmap.getWidth();

            int mult = mBitmapMod == 3 ? 1 : mBitmapMod; // 0, 1, or 2
            int offset = mult * (diff / 2);
            BitmapHelper.setBmpData(mScreen, offset, 0, mBitmap, false);
            mBitmapMod = (mBitmapMod + 1) % 4;
        }
    }

    private void drawTimer() {
        SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");

        Date date = new Date();
        String dayDate = dayFormatter.format(date);
        String timeDate = timeFormatter.format(date);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(20f);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        Bitmap textAsBitmap = Bitmap.createBitmap(mScreen.getLcdWidth(), mScreen.getLcdHeight(), ARGB_8888);
        Canvas canvas = new Canvas(textAsBitmap);
        canvas.drawText(dayDate, 0, 0.4f * mScreen.getLcdHeight(), paint);
        canvas.drawText(timeDate, 0, 0.8f * mScreen.getLcdHeight(), paint);
        mBitmap = textAsBitmap;

        mScreen.clearPixels();
        BitmapHelper.setBmpData(mScreen, 0, 0, mBitmap, false);
    }

    private void drawAllWhite() {
        for (int x = 0; x < mScreen.getLcdWidth(); x++) {
            for (int y = 0; y < mScreen.getLcdHeight(); y++) {
                mScreen.setPixel(x, y, true);
            }
        }
    }

    private final Runnable mDrawRunnable = new Runnable() {
        /**
         * Updates the display and tick counter.
         */
        @Override
        public void run() {
            // exit Runnable if the device is already closed
            if (mScreen == null) {
                return;
            }
            mTick++;
            try {
                switch (mMode) {
                    case BITMAP:
                        drawMovingBitmap();
                    case DOTS:
                        drawExpandingDots();
                        break;
                    case TIMER:
                        drawTimer();
                        break;
                    case ALL_WHITE:
                        drawAllWhite();
                        break;
                    case CROSS_HAIRS:
                        drawCrosshairs();
                        break;
                }
                mScreen.show();
                mHandler.postDelayed(this, 1000 / FPS);
            } catch (IOException e) {
                Log.e(TAG, "Exception during screen update", e);
            }
        }
    };
}