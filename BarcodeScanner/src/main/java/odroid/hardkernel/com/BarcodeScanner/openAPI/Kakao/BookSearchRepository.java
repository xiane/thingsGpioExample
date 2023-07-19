package odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao;

import android.util.Log;

import java.util.List;

import odroid.hardkernel.com.BarcodeScanner.OpenApiDefault;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookSearchRepository {
    private static BookSearchRepository instance;

    public static BookSearchRepository getInstance() {
        if (instance == null)
            instance = new BookSearchRepository();

        return instance;
    }

    private BookSearchRepository() {}

    public void getBookInfo(String isbn, int page, int size, BookResponseListener listener) {
        if (isbn != null) {
            Call<BookInfo> call = RetrofitNet.getInstance()
                    .getSearchBookInfoService()
                    .getBookInfo("isbn", page, size, isbn,
                            "KakaoAK " + OpenApiDefault.KAKAO_REST_API_APP_KEY);

            call.enqueue(new Callback<BookInfo>() {
                @Override
                public void onResponse(Call<BookInfo> call, Response<BookInfo> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            List<Document> bookInfoList = response.body().getDocuments();
                            for (int i = 0; i < bookInfoList.size(); i++) {
                                Log.d("BookSearch",
                                        "[GET] getBookInfo : " + bookInfoList.get(i).getTitle());
                            }
                            listener.onSuccessResponse(response.body());
                        }
                    }
                }

                @Override
                public void onFailure(Call<BookInfo> call, Throwable t) {
                    listener.onFailResponse();
                }
            });
        }
    }

    public interface BookResponseListener {
        void onSuccessResponse(BookInfo bookInfo);
        void onFailResponse();
    }
}
