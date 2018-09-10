package cordova.plugin.dbxp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ApiClient {

    private static Retrofit retrofit = null;

    private ApiClient() {
        // Private constructor to disable instantiation
    }

    /**
     * Gets cached {@link Retrofit} instance if available, otherwise creates and returns new instance
     *
     * @param baseUrl base URL of the APIs
     * @return {@link Retrofit} instance
     */
    public static Retrofit getClient(String baseUrl) {
        if (null == retrofit) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(client).build();
        }
        return retrofit;
    }
}