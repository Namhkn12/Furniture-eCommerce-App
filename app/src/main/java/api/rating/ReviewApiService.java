package api.rating;

import com.ndroid.ecommerce.ui.review.ReviewModel;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReviewApiService {
//    @GET("reviews")
    @GET("/ratings")
    Call<List<ReviewModel>> getReviews(
        @Query("productId") int productId
    );
    @POST("/")
    Call<ReviewModel> createRating(@Body ReviewModel reviewModel);

    @GET("/average/{productId}")
    Call<Double> getAverageRating(@Path("productId") int productId);

    @GET("/count/{productId}")
    Call<Long> getRatingCount(@Path("productId") int productId);

    @Multipart
    @POST("image/{id}")
    Call<String> uploadImage(@Path("id") int id, @Part MultipartBody.Part file);
}