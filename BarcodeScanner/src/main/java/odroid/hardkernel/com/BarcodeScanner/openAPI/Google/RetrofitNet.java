package odroid.hardkernel.com.BarcodeScanner.openAPI.Google;

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

    public VolumesInfoService getSearchVolumesInfoService() {
        Retrofit googleRetrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(OpenApiDefault.GOOGLE_BASE_URL)
                .build();

        return googleRetrofit.create(VolumesInfoService.class);
    }

}
