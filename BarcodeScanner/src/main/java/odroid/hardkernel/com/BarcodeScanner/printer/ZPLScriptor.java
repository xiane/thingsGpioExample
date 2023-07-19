package odroid.hardkernel.com.BarcodeScanner.printer;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: PageSize and dotPerInch should be used to LabelLayoutGenerator.
 */
public class ZPLScriptor {
    private final String TAG = ZPLScriptor.class.getName();
    public static class PaperSize {
        public int x;
        public int y;

        public PaperSize(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    private PaperSize paperSize;
    private int dotPerInch;
    private final String start = "^XA";
    private final String end = "^XZ";

    private String printScript = "";
    private List<Content> contents;

    public ZPLScriptor(PaperSize paperSize, int dotPerInch) {
        this.paperSize = paperSize;
        this.dotPerInch = dotPerInch;

        contents = new ArrayList<>();
    }

    public void addContents(Content content) {
        contents.add(content);
    }

    public void addContents(List<Content> contentList) {
        contentList.forEach(content -> contents.add(content));
    }
    public String getPrintScript() {
        printScript += start;
        contents.forEach(content -> printScript += content.getContent());
        printScript += end;

        Log.d(TAG, "script - " + printScript);
        return printScript;
    }
}
