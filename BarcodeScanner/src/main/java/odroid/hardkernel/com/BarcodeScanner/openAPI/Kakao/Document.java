package odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao;

import androidx.annotation.NonNull;

import java.util.List;
//import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//@Generated("jsonschema2pojo")
public class Document {

    @SerializedName("authors")
    @Expose
    private List<String> authors;
    @SerializedName("contents")
    @Expose
    private String contents;
    @SerializedName("datetime")
    @Expose
    private String datetime;
    @SerializedName("isbn")
    @Expose
    private String isbn;
    @SerializedName("price")
    @Expose
    private Integer price;
    @SerializedName("publisher")
    @Expose
    private String publisher;
    @SerializedName("sale_price")
    @Expose
    private Integer salePrice;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("translators")
    @Expose
    private List<String> translators;
    @SerializedName("url")
    @Expose
    private String url;

    @NonNull
    @Override
    public String toString() {
        return "Document: title - " + title + "\n"
                + "contents - " + contents + "\n"
                + "url - " + url + "\n"
                + "isbn - " + isbn + "\n"
                + "datetime - " + datetime + "\n"
                + "authors - " + authors + "\n"
                + "publisher - " + publisher + "\n"
                + "translators - " + translators + "\n"
                + "price - " + price + "\n"
                + "sale_price - " + salePrice + "\n"
                + "thumbnail - " + thumbnail + "\n"
                + "status - " + status;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTranslators() {
        return translators;
    }

    public void setTranslators(List<String> translators) {
        this.translators = translators;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
