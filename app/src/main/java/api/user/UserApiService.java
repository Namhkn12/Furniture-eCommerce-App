package api.user;

import java.util.List;

import api.user.request.ChangePasswordRequest;
import api.user.request.CodeRequest;
import api.user.request.ConfirmEmailRequest;
import api.user.request.LoginRequest;
import api.user.request.RegisterRequest;
import api.user.request.UpdateDOBRequest;
import api.user.request.UpdateEmailRequest;
import api.user.request.UpdateGenderRequest;
import api.user.request.UpdateNameRequest;
import api.user.request.UpdatePasswordRequest;
import api.user.request.UpdatePhoneRequest;
import api.user.request.UserAddressDTO;
import model.user.UserAddress;
import model.user.UserInfo;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApiService {
    //Image is from different endpoint - Glide
    @GET("users/{id}")
    Call<UserInfo> getUser(@Path("id") int id);

    @GET("users/{id}/address")
    Call<List<UserAddress>> getAddressesOfUser(@Path("id") int userId);

    @PUT("users/{id}/update/name")
    Call<String> changeName(@Path("id") int id, @Body UpdateNameRequest request);

    @PUT("users/{id}/update/gender")
    Call<String> changeGender(@Path("id") int id, @Body UpdateGenderRequest request);

    @PUT("users/{id}/update/dob")
    Call<String> changeDob(@Path("id") int id, @Body UpdateDOBRequest request);

    @PUT("users/{id}/update/phone")
    Call<String> changePhoneNumber(@Path("id") int id, @Body UpdatePhoneRequest request);

    @PUT("users/update/email")
    Call<Boolean> updateEmail(@Body UpdateEmailRequest request);

    @POST("users/request-confirm-email")
    Call<Boolean> requestConfirmEmail(@Body ConfirmEmailRequest request);

    @POST("users/verify-email")
    Call<Boolean> verifyEmailCode(@Body CodeRequest codeRequest);

    @PUT("users/update/password")
    Call<Boolean> updatePassword(@Body UpdatePasswordRequest request);

    @POST("users/forgot-password")
    Call<Boolean> requestPasswordChange(@Body ChangePasswordRequest request);

    @PUT("users/{id}/update/address")
    Call<String> updateAddress(@Path("id") int id, @Body UserAddressDTO request);

    @POST("users/{id}/add/address")
    Call<Void> addAddress(@Path("id") int userId, @Body UserAddressDTO addressDTO);

    @PUT("users/remove/address/{id}")
    Call<Void> removeAddress(@Path("id") int addressId);

    @POST("login")
    Call<UserInfo> login(@Body LoginRequest request);

    @POST("register")
    Call<Boolean> register(@Body RegisterRequest request);

    @Multipart
    @POST("image/users/{id}")
    Call<String> uploadImage(@Path("id") int id, @Part MultipartBody.Part file);
}
