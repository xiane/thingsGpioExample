package odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface BookInfoService {
    @GET("/v3/search/book")
    Call<BookInfo> getBookInfo(@Query("target") String target, @Query("page") int page, @Query ("size") int size, @Query("query") String isbn, @Header("Authorization") String apiKey);
}
