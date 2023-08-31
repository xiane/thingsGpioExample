package odroid.hardkernel.com.BarcodeScanner;

import android.annotation.SuppressLint;
import android.hardkernel.com.BarcodeScanner.R;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.hardkernel.odroid.things.contrib.BarcodeScanner.BarcodeScanner;

import odroid.hardkernel.com.BarcodeScanner.Views.BookItemView;
import odroid.hardkernel.com.BarcodeScanner.Views.BookListViewAdapter;

public class MainActivity extends AppCompatActivity {
    final String TAG = "BarcodeScanner";

    final ArrayList<BookItemView> arrayList = new ArrayList<>();

    private BarcodeScanner scanner;

    private Boolean print = false;

    private final Printer zebraPrinter = script -> {
        if (print) {
            try {
                File printer = new File("/dev/usb/lp0");
                FileWriter fileWriter = new FileWriter(printer);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(script);
                writer.flush();
                writer.close();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private final AddItemCallback callback = (thumbnailLink, title, descript) -> {
        arrayList.add(new BookItemView(thumbnailLink, title, descript));
        BookListViewAdapter bookListViewAdapter =
                new BookListViewAdapter(getApplicationContext(), arrayList);
        ListView booListView = findViewById(R.id.bookListView);
        booListView.setAdapter(bookListViewAdapter);
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button barcode_scan = findViewById(R.id.barcode_scan_btn);

        Switch printSw = findViewById(R.id.print);
        printSw.setChecked(print);

        printSw.setOnCheckedChangeListener((compoundButton, b) -> print = b);

        // It also can be KakaoBarcodeListener
        BarcodeScanner.BarcodeListener listener = new GoogleBarcodeListener(zebraPrinter, callback);

        try {
            scanner = new BarcodeScanner(BoardDefaults.getTriggerPin(),
                    BoardDefaults.getResetPin(), BoardDefaults.getUARTPort(), listener);
            scanner.setSuffix((byte) 13);

            scanner.setScanMode(BarcodeScanner.ScanMode.manual);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        barcode_scan.setOnClickListener(view -> {
            Log.d(TAG, "start scan");
            try {
                scanner.startScan();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
