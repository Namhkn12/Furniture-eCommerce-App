package com.example.baitaplonandroid.api;


import com.example.baitaplonandroid.ReviewModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewApiService {
//    @GET("reviews")
    @GET("/ratings")
    Call<List<ReviewModel>> getReviews(
        @Query("userId") int userId,
        @Query("productId") int productId
    );
    @POST("/")
    Call<ReviewModel> createRating(@Body ReviewModel reviewModel);
}