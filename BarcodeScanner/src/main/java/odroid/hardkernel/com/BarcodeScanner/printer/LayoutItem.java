package odroid.hardkernel.com.BarcodeScanner.printer;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LayoutItem {
    public enum Type {
        Text,
        ISBN,
        QR
    }
    private Type type;
    private String[] options;
    private String value;
}
