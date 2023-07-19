package odroid.hardkernel.com.BarcodeScanner.printer;

import java.util.ArrayList;
import java.util.List;

public class LabelLayoutGenerator {
    private int originX, originY;
    private int fontSizeX, fontSizeY;
    private int ISBNSizeY;
    private int QRSizeY;
    private int gap;
    private int charPerLine;

    private int ISBNSizeWidth = 0;
    private int QRSizeWidth = 0;

    public LabelLayoutGenerator() {
        ISBNSizeY = 100;
        QRSizeY = 330;
        gap = 10;
        charPerLine = 50;
    }

    public void setOrigin(int x, int y) {
        originX = x;
        originY = y;
    }

    public void setFontSize(int x, int y) {
        fontSizeX = x;
        fontSizeY = y;
    }

    public void setISBNSizeWidth(int ISBNSizeWidth) {
        this.ISBNSizeWidth = ISBNSizeWidth;
    }

    public void setQRSizeWidth(int QRSizeWidth) {
        this.QRSizeWidth = QRSizeWidth;
    }

    public List<Content> generateLayout (List<LayoutItem> items) {
        ArrayList<Content> contents = new ArrayList<>();
        for (LayoutItem item: items) {
            switch (item.getType()) {
                case Text:
                    Text text;
                    char[] totalText = item.getValue().toCharArray();
                    for (int i=0; i < totalText.length; i+= charPerLine) {
                        int count = Math.min((totalText.length - i), charPerLine);
                        String textPart = new String(totalText, i, count);
                        String[] newOptions = new String[] {item.getOptions()[0],
                                String.valueOf(fontSizeX),
                                String.valueOf(fontSizeY)};
                        text = new Text(newOptions);
                        text.fieldOrigin(originX, originY);
                        text.setValue(textPart);
                        contents.add(text);
                        originY += fontSizeY + gap;
                    }
                    break;
                case ISBN:
                    Barcode barcode = new Barcode(Barcode.Type.EAN13, item.getOptions());
                    barcode.fieldOrigin(originX, originY);
                    if (ISBNSizeWidth == 0)
                        ISBNSizeWidth = 3;
                    barcode.setBarcodeField(new String[] {String.valueOf(ISBNSizeWidth)});
                    contents.add(barcode);
                    String isbn = item.getValue();
                    if (isbn.length() != 10|isbn.length() != 13) {
                        String[] isbns = isbn.split(" ");
                        for(String code: isbns)
                            if (code.length() == 13)
                                isbn = code;
                    }
                    barcode.setValue(isbn);
                    originY += ISBNSizeY + gap;
                    break;
                case QR:
                    Barcode qr = new Barcode(Barcode.Type.QRCode, item.getOptions());
                    qr.fieldOrigin(originX, originY);
                    if (QRSizeWidth == 0)
                        QRSizeWidth = 5;
                    qr.setBarcodeField(new String[]{String.valueOf(QRSizeWidth)});
                    qr.setValue(item.getValue());
                    contents.add(qr);
                    originY += QRSizeY + gap;
                    break;
            }
        }

        return contents;
    }
}

