package odroid.hardkernel.com.BarcodeScanner.Views;

public class BookItemView {
    private String imgUrl;
    private String bookTitle;
    private String bookDesc;

    public BookItemView(String imageUrl, String title, String descript) {
        this.imgUrl = imageUrl;
        bookTitle = title;
        bookDesc = descript;
    }

    public String getImageUrl() {
        return imgUrl;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookDesc() {
        return bookDesc;
    }
}
