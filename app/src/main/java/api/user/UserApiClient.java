package api.user;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UserApiClient {
    private static final String USER_ENDPOINT_URL = "http://192.168.1.101:8080/api/";
    private static Retrofit userRetrofit;

    public static Retrofit getRetrofit() {
        if (userRetrofit == null) {
            userRetrofit = new Retrofit.Builder()
                    .baseUrl(USER_ENDPOINT_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return userRetrofit;
    }
}
