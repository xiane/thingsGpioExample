package odroid.hardkernel.com.BarcodeScanner.printer;

public interface Content {
    final String fieldOrigin = "^FO";
    final String fieldSeparate = "^FS";
    final String fieldData = "^FD";

    public abstract void fieldOrigin(int x, int y);

    public abstract void setValue(String value);
    public abstract String getContent();
}
