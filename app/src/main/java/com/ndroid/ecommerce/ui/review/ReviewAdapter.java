//package com.example.baitaplonandroid;
//
////package com.example.reviewapp;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RatingBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
//
//    private List<ReviewModel> reviewList;
//
//    public ReviewAdapter(List<ReviewModel> reviewList) {
//        this.reviewList = reviewList;
//    }
//
//    @NonNull
//    @Override
//    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
//        return new ReviewViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
//        ReviewModel review = reviewList.get(position);
//
//        holder.username.setText(review.getUsername());
//        holder.ratingBar.setRating(review.getRating());
//        holder.ratingText.setText(review.getRatingText());
//        holder.content.setText(review.getContent());
//        holder.date.setText(review.getDate());
//
//        // Handle review image
//        if (review.isHasImage()) {
//            holder.reviewImage.setVisibility(View.VISIBLE);
//            holder.reviewImage.setImageResource(review.getImageResourceId());
//        } else {
//            holder.reviewImage.setVisibility(View.GONE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return reviewList.size();
//    }
//
//    static class ReviewViewHolder extends RecyclerView.ViewHolder {
//        CircleImageView avatar;
//        TextView username;
//        RatingBar ratingBar;
//        TextView ratingText;
//        TextView content;
//        ImageView reviewImage;
//        TextView date;
//
//        public ReviewViewHolder(@NonNull View itemView) {
//            super(itemView);
//            avatar = itemView.findViewById(R.id.img_user_avatar);
//            username = itemView.findViewById(R.id.tv_username);
//            ratingBar = itemView.findViewById(R.id.rating_bar_review);
//            ratingText = itemView.findViewById(R.id.tv_review_rating);
//            content = itemView.findViewById(R.id.tv_review_content);
//            reviewImage = itemView.findViewById(R.id.img_review_photo);
//            date = itemView.findViewById(R.id.tv_review_date);
//        }
//    }
//}
package com.ndroid.ecommerce.ui.review;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ndroid.ecommerce.R;

import java.util.List;

import api.rating.ReviewClient;
import api.user.UserApiClient;
import api.user.UserApiService;
import de.hdodenhof.circleimageview.CircleImageView;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<ReviewModel> reviewList;
    private final UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    public ReviewAdapter(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);

        userApiService.getUser(review.getUserId()).enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    holder.username.setText(response.body().getDisplayName());
                } else holder.username.setText("Unknown User");
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable throwable) {
                holder.username.setText("Unknown User");
            }
        });

        holder.ratingBar.setRating(review.getRating());
        holder.ratingText.setText(review.getRatingText());
        holder.content.setText(review.getContent());
        holder.date.setText(review.getDate());

        // Handle review image
        if (review.isHasImage()) {
            holder.reviewImage.setVisibility(View.VISIBLE);

            // If we have an image URL, load it with Glide
            if (review.getImageUrl() != null && !review.getImageUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(ReviewClient.REVIEW_ENDPOINT_URL + "image/" + review.getId())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_report_image)
                        .into(holder.reviewImage);
            } else {
                // Fallback to resource ID
                holder.reviewImage.setImageResource(review.getImageResourceId());
            }
        } else {
            holder.reviewImage.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext()).load(UserApiClient.USER_ENDPOINT_URL + "image/users/" + review.getUserId())
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView username;
        RatingBar ratingBar;
        TextView ratingText;
        TextView content;
        ImageView reviewImage;
        TextView date;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.img_user_avatar);
            username = itemView.findViewById(R.id.tv_username);
            ratingBar = itemView.findViewById(R.id.rating_bar_review);
            ratingText = itemView.findViewById(R.id.tv_review_rating);
            content = itemView.findViewById(R.id.tv_review_content);
            reviewImage = itemView.findViewById(R.id.img_review_photo);
            date = itemView.findViewById(R.id.tv_review_date);
        }
    }
}