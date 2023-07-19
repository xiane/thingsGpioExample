package odroid.hardkernel.com.BarcodeScanner.openAPI.Google;

import android.util.Log;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VolumesSearchRepository {
    private static VolumesSearchRepository instance;

    public static VolumesSearchRepository getInstance() {
        if (instance == null)
            instance = new VolumesSearchRepository();

        return instance;
    }

    private VolumesSearchRepository() {}

    public void getVolumeInfo(String isbn, VolumeInfoResponseListener listener) {
        if (isbn != null) {
            Call<Volumes> call = RetrofitNet.getInstance()
                    .getSearchVolumesInfoService()
                    .getVolumesInfo("isbn:" + isbn);
            call.enqueue(new Callback<Volumes>() {
                @Override
                public void onResponse(Call<Volumes> call, Response<Volumes> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Volumes volumes = response.body();
                            if (volumes.getTotalItems() > 0) {
                                VolumeInfo volumeInfo = response.body().getItems().get(0).getVolumeInfo();
                                Log.d("VolumeSearch",
                                        "[GET] getVolumeInfo : " + volumeInfo.getTitle());
                                listener.onSuccessResponse(volumeInfo);
                            } else {
                                listener.onFailResponse();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<Volumes> call, Throwable t) {
                    listener.onFailResponse();
                }
            });
        }
    }

    public interface VolumeInfoResponseListener {
        void onSuccessResponse(VolumeInfo volume);
        void onFailResponse();
    }
}
