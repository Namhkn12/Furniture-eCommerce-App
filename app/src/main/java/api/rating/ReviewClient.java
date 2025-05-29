package api.rating;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReviewClient {
    private static Retrofit retrofit;
    public static final String REVIEW_ENDPOINT_URL = "http://14.189.204.107:25504/";

    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(REVIEW_ENDPOINT_URL) // sửa lại đúng URL backend của bạn
                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
