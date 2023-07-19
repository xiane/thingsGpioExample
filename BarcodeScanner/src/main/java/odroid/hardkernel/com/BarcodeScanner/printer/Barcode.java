package odroid.hardkernel.com.BarcodeScanner.printer;

import android.util.Log;

public class Barcode implements Content {
    private String barcode = "^B";
    private String barcodeField = "^BY";

    private int x,y;
    private String value = "";
    private Type type;

    public enum Type {
        Aztec,
        Code11,
        Interleaved2Of5,
        Code39,
        Code49,
        PlanetCode,
        PDF417,
        EAN8,
        UPCE,
        Code93,
        CODABLOCK,
        Code128,
        UPSMaxiCode,
        EAN13,
        MicroPDF417,
        Industrial2Of5,
        Standard2Of5,
        ANSICodabar,
        LOGMARS,
        MSI,
        Plessey,
        QRCode,
        RSS,
        UPCEAN,
        TLC39,
        UPCA,
        DataMatrix,
        POSTNET
    }

    public Barcode(Type code, String[] options) {
        type = code;
        switch (code) {
            case Aztec:
                this.barcode += "0";
                break;
            case EAN13:
                this.barcode += "E";
                break;
            case QRCode:
                this.barcode += "Q";
                break;
            default:
                Log.d(ZPLScriptor.class.getName(),
                        "this barcode ["+ code.name() +"] is not implemented");
        }

        for(int i = 0; i < options.length; i++) {
            this.barcode += options[i];

            if (i < options.length -1)
                this.barcode += ",";
        }
    }

    @Override
    public void fieldOrigin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setValue(String value) {
        if (type == Type.QRCode)
            this.value += "AAA";
        this.value += value;
    }

    public void setBarcodeField(String[] options) {
        barcodeField += options[0];
        for (int i=1; i< options.length; i++) {
            barcodeField += ",";
            barcodeField += options[i];
        }
    }

    @Override
    public String getContent() {
        String content;
        content = fieldOrigin + x + "," + y;
        content += barcode;
        content += barcodeField;
        content += fieldData + value;
        content += fieldSeparate;

        return content;
    }
}
