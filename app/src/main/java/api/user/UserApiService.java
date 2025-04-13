package api.user;

import model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserApiService {
    //Image is from different endpoint
    @GET("users/{id}")
    Call<User> getUser(@Path("id") int id);
}
