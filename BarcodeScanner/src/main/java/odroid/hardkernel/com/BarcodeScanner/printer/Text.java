package odroid.hardkernel.com.BarcodeScanner.printer;

public class Text implements Content{
    private String font = "^A";
    private int x, y;
    String value;

    public Text(String[] options) throws IllegalArgumentException {
        if (options.length < 1)
            throw new IllegalArgumentException("option must contains font");
        font += options[0];
        if (options[0].toCharArray()[0] <= '9'
                |options[0].toCharArray()[0] >= '0')
            font += ",";

        for(int i = 1; i < options.length; i++) {
            font += options[i];
            if(i != options.length -1)
                font += ",";
        }
    }

    @Override
    public void fieldOrigin(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getContent() {
        String content;
        content = fieldOrigin + x + "," + y;
        content += font;
        content += fieldData + value;
        content += fieldSeparate;

        return content;
    }
}
