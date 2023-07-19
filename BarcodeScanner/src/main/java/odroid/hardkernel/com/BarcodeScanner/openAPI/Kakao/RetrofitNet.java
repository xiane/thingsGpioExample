package odroid.hardkernel.com.BarcodeScanner.openAPI.Kakao;

import odroid.hardkernel.com.BarcodeScanner.OpenApiDefault;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitNet {
    private static RetrofitNet instance;

    public static RetrofitNet getInstance() {
        if (instance == null)
            instance = new RetrofitNet();
        return instance;
    }

    private  RetrofitNet() {}

    public BookInfoService getSearchBookInfoService() {
        Retrofit kakaoRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(OpenApiDefault.KAKAO_BASE_URL)
                .build();

        return kakaoRetrofit.create(BookInfoService.class);
    }
}
