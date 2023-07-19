package odroid.hardkernel.com.BarcodeScanner.openAPI.Google;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VolumesInfoService {
    @GET("/books/v1/volumes")
    Call<Volumes> getVolumesInfo(@Query("q") String query);
}
