package com.example.baitaplonandroid;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<ReviewModel> allReviewsList;
    private List<ReviewModel> filteredReviewsList;

    private TextView tabAllReviews, tabHasPicture, tabByRating;
    private Button btnSubmitComment, btnAddPhoto;
    private EditText editComment;
    private RatingBar ratingBarNewComment;

    private boolean hasSelectedPhoto = false;
    private float userRating = 5.0f; // Default rating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize filter tabs
        tabAllReviews = findViewById(R.id.tab_all_reviews);
        tabHasPicture = findViewById(R.id.tab_has_picture);
        tabByRating = findViewById(R.id.tab_by_rating);

        // Initialize comment section
        btnSubmitComment = findViewById(R.id.btn_submit_comment);
        btnAddPhoto = findViewById(R.id.btn_add_photo);
        editComment = findViewById(R.id.edit_comment);
        ratingBarNewComment = findViewById(R.id.rating_bar_new_comment);

        // Ensure rating bar is interactive
        ratingBarNewComment.setIsIndicator(false);

        // Set up rating bar listener
        ratingBarNewComment.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG, "Rating changed to: " + rating + " (fromUser: " + fromUser + ")");
                userRating = rating;
//                Toast.makeText(ReviewActivity.this, "Rating: " + rating, Toast.LENGTH_SHORT).show();
            }
        });

        // Create sample data
        createSampleData();

        // Set up adapter with all reviews initially
        filteredReviewsList = new ArrayList<>(allReviewsList);
        adapter = new ReviewAdapter(filteredReviewsList);
        recyclerView.setAdapter(adapter);

        // Set up tab click listeners
        setupTabListeners();

        // Set up comment submission
        setupCommentSubmission();
    }

    private void setupTabListeners() {
        tabAllReviews.setOnClickListener(v -> {
            updateTabSelection(tabAllReviews);
            filteredReviewsList.clear();
            filteredReviewsList.addAll(allReviewsList);
            adapter.notifyDataSetChanged();
        });

        tabHasPicture.setOnClickListener(v -> {
            updateTabSelection(tabHasPicture);
            filteredReviewsList.clear();
            for (ReviewModel review : allReviewsList) {
                if (review.isHasImage()) {
                    filteredReviewsList.add(review);
                }
            }
            adapter.notifyDataSetChanged();
        });

        tabByRating.setOnClickListener(v -> {
            updateTabSelection(tabByRating);
            filteredReviewsList.clear();
            filteredReviewsList.addAll(allReviewsList);

            // Sort by rating (highest first)
            Collections.sort(filteredReviewsList, new Comparator<ReviewModel>() {
                @Override
                public int compare(ReviewModel r1, ReviewModel r2) {
                    return Float.compare(r2.getRating(), r1.getRating());
                }
            });

            adapter.notifyDataSetChanged();
        });
    }

    private void updateTabSelection(TextView selectedTab) {
        // Reset all tabs
        tabAllReviews.setBackground(getResources().getDrawable(R.drawable.tab_unselected_background));
        tabAllReviews.setTextColor(getResources().getColor(android.R.color.darker_gray));

        tabHasPicture.setBackground(getResources().getDrawable(R.drawable.tab_unselected_background));
        tabHasPicture.setTextColor(getResources().getColor(android.R.color.darker_gray));

        tabByRating.setBackground(getResources().getDrawable(R.drawable.tab_unselected_background));
        tabByRating.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Set selected tab
        selectedTab.setBackground(getResources().getDrawable(R.drawable.tab_selected_background));
        selectedTab.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
    }

    private void setupCommentSubmission() {
        btnAddPhoto.setOnClickListener(v -> {
            // In a real app, you would launch a photo picker here
            Toast.makeText(ReviewActivity.this, "Photo selection would open here", Toast.LENGTH_SHORT).show();
            hasSelectedPhoto = true;
        });

        btnSubmitComment.setOnClickListener(v -> {
            String commentText = editComment.getText().toString().trim();

            if (commentText.isEmpty()) {
                Toast.makeText(ReviewActivity.this, "Please write a comment", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log the current rating value
            Log.d(TAG, "Submitting comment with rating: " + userRating);

            // Create new review
            ReviewModel newReview = new ReviewModel();
            newReview.setUsername("You");
            newReview.setRating(userRating); // Use the rating from the listener
            newReview.setRatingText(getRatingText(userRating));
            newReview.setContent(commentText);

            // Get current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            newReview.setDate(currentDateTime);

            // Set image if added
            newReview.setHasImage(hasSelectedPhoto);
            if (hasSelectedPhoto) {
                newReview.setImageResourceId(android.R.drawable.ic_menu_gallery);
            }

            // Add to lists
            allReviewsList.add(0, newReview);

            // Update filtered list based on current tab
            if (tabAllReviews.getBackground().getConstantState().equals(
                    getResources().getDrawable(R.drawable.tab_selected_background).getConstantState())) {
                filteredReviewsList.add(0, newReview);
            } else if (tabHasPicture.getBackground().getConstantState().equals(
                    getResources().getDrawable(R.drawable.tab_selected_background).getConstantState())) {
                if (newReview.isHasImage()) {
                    filteredReviewsList.add(0, newReview);
                }
            } else if (tabByRating.getBackground().getConstantState().equals(
                    getResources().getDrawable(R.drawable.tab_selected_background).getConstantState())) {
                filteredReviewsList.add(0, newReview);
                // Re-sort by rating
                Collections.sort(filteredReviewsList, new Comparator<ReviewModel>() {
                    @Override
                    public int compare(ReviewModel r1, ReviewModel r2) {
                        return Float.compare(r2.getRating(), r1.getRating());
                    }
                });
            }

            adapter.notifyDataSetChanged();

            // Reset form
            editComment.setText("");
            ratingBarNewComment.setRating(5); // Reset to 5 stars
            userRating = 5.0f; // Reset the stored rating
            hasSelectedPhoto = false;

            Toast.makeText(ReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private String getRatingText(float rating) {
        if (rating >= 4.5) return "Đánh giá: Tốt";
        else if (rating >= 3.5) return "Đánh giá: Tạm tạm";
        else if (rating >= 2.5) return "Đánh giá: Trung bình";
        else return "Đánh giá: Kém";
    }

    private void createSampleData() {
        allReviewsList = new ArrayList<>();

        // First review
        ReviewModel review1 = new ReviewModel();
        review1.setUsername("Lycoon");
        review1.setRating(4);
        review1.setRatingText("Đánh giá: Tốt");
        review1.setContent("Hàng vip nha mọi người");
        review1.setDate("15/3/2025 05:54");
        review1.setHasImage(false);
        allReviewsList.add(review1);

        // Second review
        ReviewModel review2 = new ReviewModel();
        review2.setUsername("Rina");
        review2.setRating(4);
        review2.setRatingText("Đánh giá: Tạm tạm");
        review2.setContent("Không như quảng cáo");
        review2.setDate("10/3/2025 16:20");
        review2.setHasImage(true);
        review2.setImageResourceId(R.drawable.couch);
        allReviewsList.add(review2);

        // Add more sample reviews with different ratings for testing
        ReviewModel review3 = new ReviewModel();
        review3.setUsername("Alex");
        review3.setRating(5);
        review3.setRatingText("Đánh giá: Tốt");
        review3.setContent("Sản phẩm tuyệt vời!");
        review3.setDate("08/3/2025 10:15");
        review3.setHasImage(true);
        review3.setImageResourceId(R.drawable.couch);
        allReviewsList.add(review3);

        ReviewModel review4 = new ReviewModel();
        review4.setUsername("Maria");
        review4.setRating(3);
        review4.setRatingText("Đánh giá: Trung bình");
        review4.setContent("Chất lượng ổn, giá hơi cao");
        review4.setDate("05/3/2025 14:30");
        review4.setHasImage(false);
        allReviewsList.add(review4);
    }
}
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.RatingBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
//public class ReviewActivity extends AppCompatActivity {
//
//    private RecyclerView recyclerView;
//    private ReviewAdapter adapter;
//    private List<ReviewModel> allReviewsList;
//    private List<ReviewModel> filteredReviewsList;
//
//    private TextView tabAllReviews, tabHasPicture, tabByRating;
//    private Button btnSubmitComment, btnAddPhoto;
//    private EditText editComment;
//    private RatingBar ratingBarNewComment;
//
//    private boolean hasSelectedPhoto = false;
//    private float userRating = 5.0f; // Default rating
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review);
//
//        // Initialize RecyclerView
//        recyclerView = findViewById(R.id.recycler_reviews);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // Initialize filter tabs
//        tabAllReviews = findViewById(R.id.tab_all_reviews);
//        tabHasPicture = findViewById(R.id.tab_has_picture);
//        tabByRating = findViewById(R.id.tab_by_rating);
//
//        // Initialize comment section
//        btnSubmitComment = findViewById(R.id.btn_submit_comment);
//        btnAddPhoto = findViewById(R.id.btn_add_photo);
//        editComment = findViewById(R.id.edit_comment);
//        ratingBarNewComment = findViewById(R.id.rating_bar_new_comment);
//
//        // Create sample data
//        createSampleData();
//
//        // Set up adapter with all reviews initially
//        filteredReviewsList = new ArrayList<>(allReviewsList);
//        adapter = new ReviewAdapter(filteredReviewsList);
//        recyclerView.setAdapter(adapter);
//
//        // Set up tab click listeners
//        setupTabListeners();
//
//        // Set up rating bar listener
//        setupRatingBarListener();
//
//        // Set up comment submission
//        setupCommentSubmission();
//    }
//
//    private void setupRatingBarListener() {
//        ratingBarNewComment.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            @Override
//            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                if (fromUser) {
//                    userRating = rating;
//                    // Optional: Show a toast with the selected rating
//                    Toast.makeText(ReviewActivity.this,
//                            "Selected rating: " + rating, Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    private void setupTabListeners() {
//        tabAllReviews.setOnClickListener(v -> {
//            updateTabSelection(tabAllReviews);
//            filteredReviewsList.clear();
//            filteredReviewsList.addAll(allReviewsList);
//            adapter.notifyDataSetChanged();
//        });
//
//        tabHasPicture.setOnClickListener(v -> {
//            updateTabSelection(tabHasPicture);
//            filteredReviewsList.clear();
//            for (ReviewModel review : allReviewsList) {
//                if (review.isHasImage()) {
//                    filteredReviewsList.add(review);
//                }
//            }
//            adapter.notifyDataSetChanged();
//        });
//
//        tabByRating.setOnClickListener(v -> {
//            updateTabSelection(tabByRating);
//            filteredReviewsList.clear();
//            filteredReviewsList.addAll(allReviewsList);
//
//            // Sort by rating (highest first)
//            Collections.sort(filteredReviewsList, new Comparator<ReviewModel>() {
//                @Override
//                public int compare(ReviewModel r1, ReviewModel r2) {
//                    return Float.compare(r2.getRating(), r1.getRating());
//                }
//            });
//
//            adapter.notifyDataSetChanged();
//        });
//    }
//
//    private void updateTabSelection(TextView selectedTab) {
//        // Reset all tabs
//        tabAllReviews.setBackground(getResources().getDrawable(R.drawable.tab_unselected_background));
//        tabAllReviews.setTextColor(getResources().getColor(android.R.color.darker_gray));
//
//        tabHasPicture.setBackground(getResources().getDrawable(R.drawable.tab_unselected_background));
//        tabHasPicture.setTextColor(getResources().getColor(android.R.color.darker_gray));
//
//        tabByRating.setBackground(getResources().getDrawable(R.drawable.tab_unselected_background));
//        tabByRating.setTextColor(getResources().getColor(android.R.color.darker_gray));
//
//        // Set selected tab
//        selectedTab.setBackground(getResources().getDrawable(R.drawable.tab_selected_background));
//        selectedTab.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
//    }
//
//    private void setupCommentSubmission() {
//        btnAddPhoto.setOnClickListener(v -> {
//            // In a real app, you would launch a photo picker here
//            Toast.makeText(ReviewActivity.this, "Photo selection would open here", Toast.LENGTH_SHORT).show();
//            hasSelectedPhoto = true;
//        });
//
//        btnSubmitComment.setOnClickListener(v -> {
//            String commentText = editComment.getText().toString().trim();
//
//            if (commentText.isEmpty()) {
//                Toast.makeText(ReviewActivity.this, "Please write a comment", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            // Create new review
//            ReviewModel newReview = new ReviewModel();
//            newReview.setUsername("You");
//            newReview.setRating(userRating); // Use the rating from the listener
//            newReview.setRatingText(getRatingText(userRating));
//            newReview.setContent(commentText);
//
//            // Get current date and time
//            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//            String currentDateTime = sdf.format(new Date());
//            newReview.setDate(currentDateTime);
//
//            // Set image if added
//            newReview.setHasImage(hasSelectedPhoto);
//            if (hasSelectedPhoto) {
//                newReview.setImageResourceId(android.R.drawable.ic_menu_gallery);
//            }
//
//            // Add to lists
//            allReviewsList.add(0, newReview);
//
//            // Update filtered list based on current tab
//            if (tabAllReviews.getBackground().getConstantState().equals(
//                    getResources().getDrawable(R.drawable.tab_selected_background).getConstantState())) {
//                filteredReviewsList.add(0, newReview);
//            } else if (tabHasPicture.getBackground().getConstantState().equals(
//                    getResources().getDrawable(R.drawable.tab_selected_background).getConstantState())) {
//                if (newReview.isHasImage()) {
//                    filteredReviewsList.add(0, newReview);
//                }
//            } else if (tabByRating.getBackground().getConstantState().equals(
//                    getResources().getDrawable(R.drawable.tab_selected_background).getConstantState())) {
//                filteredReviewsList.add(0, newReview);
//                // Re-sort by rating
//                Collections.sort(filteredReviewsList, new Comparator<ReviewModel>() {
//                    @Override
//                    public int compare(ReviewModel r1, ReviewModel r2) {
//                        return Float.compare(r2.getRating(), r1.getRating());
//                    }
//                });
//            }
//
//            adapter.notifyDataSetChanged();
//
//            // Reset form
//            editComment.setText("");
//            ratingBarNewComment.setRating(5); // Reset to 5 stars
//            userRating = 5.0f; // Reset the stored rating
//            hasSelectedPhoto = false;
//
//            Toast.makeText(ReviewActivity.this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
//        });
//    }
//
//    private String getRatingText(float rating) {
//        if (rating >= 4.5) return "Đánh giá: Tốt";
//        else if (rating >= 3.5) return "Đánh giá: Tạm tạm";
//        else if (rating >= 2.5) return "Đánh giá: Trung bình";
//        else return "Đánh giá: Kém";
//    }
//
//    private void createSampleData() {
//        allReviewsList = new ArrayList<>();
//
//        // First review
//        ReviewModel review1 = new ReviewModel();
//        review1.setUsername("Lycoon");
//        review1.setRating(4);
//        review1.setRatingText("Đánh giá: Tốt");
//        review1.setContent("Hàng vip nha mọi người");
//        review1.setDate("15/3/2025 05:54");
//        review1.setHasImage(false);
//        allReviewsList.add(review1);
//
//        // Second review
//        ReviewModel review2 = new ReviewModel();
//        review2.setUsername("Rina");
//        review2.setRating(4);
//        review2.setRatingText("Đánh giá: Tạm tạm");
//        review2.setContent("Không như quảng cáo");
//        review2.setDate("10/3/2025 16:20");
//        review2.setHasImage(true);
//        review2.setImageResourceId(R.drawable.couch);
//        allReviewsList.add(review2);
//
//        // Add more sample reviews with different ratings for testing
//        ReviewModel review3 = new ReviewModel();
//        review3.setUsername("Alex");
//        review3.setRating(5);
//        review3.setRatingText("Đánh giá: Tốt");
//        review3.setContent("Sản phẩm tuyệt vời!");
//        review3.setDate("08/3/2025 10:15");
//        review3.setHasImage(true);
//        review3.setImageResourceId(R.drawable.couch);
//        allReviewsList.add(review3);
//
//        ReviewModel review4 = new ReviewModel();
//        review4.setUsername("Maria");
//        review4.setRating(3);
//        review4.setRatingText("Đánh giá: Trung bình");
//        review4.setContent("Chất lượng ổn, giá hơi cao");
//        review4.setDate("05/3/2025 14:30");
//        review4.setHasImage(false);
//        allReviewsList.add(review4);
//    }
//
//}