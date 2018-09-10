package cordova.plugin.dbxp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ApiService {

    @GET("sdk")
    Call<ResponseBody> getConfig(@Header("Authorization") String auth, @Query("appId") String appId,
                                 @Query("entities") String entities, @Query("userId") String userId);
}