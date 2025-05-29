package com.ndroid.ecommerce.ui.review;


import static android.view.View.VISIBLE;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.MyApplication;
import com.ndroid.ecommerce.R;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import api.rating.ReviewApiService;
import api.rating.ReviewClient;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<ReviewModel> allReviewsList;
    private List<ReviewModel> filteredReviewsList;
    private RatingBar ratingBar;
    private TextView tabAllReviews, tabHasPicture, tabByRating;
    private Button btnSubmitComment, btnAddPhoto;
    private EditText editComment;
    private RatingBar ratingBarNewComment;
    private TextView ratingText, textAlertHasImage;
    private boolean hasSelectedPhoto = false;
    private float userRating = 5.0f; // Default rating
    private final ReviewApiService reviewApiService = ReviewClient.getRetrofit().create(ReviewApiService.class);

    private int productId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        productId = getIntent().getIntExtra("product_id", 0);
        Log.w("PROD_ID", String.valueOf(productId));

        textAlertHasImage = findViewById(R.id.textAlertHasImage);

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

        ratingBar = findViewById(R.id.rating_bar);
        ratingText = findViewById(R.id.tv_rating_value);
        // Create sample data
//        createSampleData();
        getReviews(productId);

        // Set up tab click listeners
        setupTabListeners();

        // Set up comment submission
        setupCommentSubmission();

    }
    float getAverageRating(List<ReviewModel> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0;

//        float total = 0f;
//        for (ReviewModel review : reviews) {
//            total += review.getRating();
//        }
//        return Math.round(total / reviews.size()); // Làm tròn về số nguyên
        float total = 0f;
        for (ReviewModel review : reviews) {
            total += review.getRating();
        }
        float avg = total / reviews.size();

        // Làm tròn tới 2 chữ số thập phân
        return Math.round(avg * 100) / 100f;
    }
    private void createReview(ReviewModel review) {
        Call<ReviewModel> call = reviewApiService.createRating(review);
        call.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewModel createdReview = response.body();
                    if (hasSelectedPhoto) {
                        uploadImage(createdReview.getId(), createdReview);//upload image
                        reloadUi();
                    } else {
                        reloadUi();
//                        updateUI(createdReview);
                    }
                    // Reset form
                    editComment.setText("");
                    ratingBarNewComment.setRating(5); // Reset to 5 stars
                    userRating = 5.0f; // Reset the stored rating
                    Log.d("API", "Review đã tạo: " + createdReview.getContent());
                    Toast.makeText(getApplicationContext(), "Review submitted!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API", "Lỗi phản hồi khi tạo review: " + response.code());
                    Toast.makeText(getApplicationContext(), "Submit failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {
                Log.e("API", "Lỗi kết nối khi tạo review", t);
                Toast.makeText(getApplicationContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getReviews(int productId) {
        Call<List<ReviewModel>> call = reviewApiService.getReviews(productId);
        call.enqueue(new Callback<List<ReviewModel>>() {
            @Override
            public void onResponse(Call<List<ReviewModel>> call, Response<List<ReviewModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewModel> reviews = response.body();
                    allReviewsList = new ArrayList<>();
                    allReviewsList.addAll(reviews);

                    filteredReviewsList = new ArrayList<>(allReviewsList);
                    float averageRating = getAverageRating(filteredReviewsList);
                    Log.d("Average", "Rating trung bình: " + averageRating);

                    ratingBar.setRating(averageRating);
                    ratingText.setText(averageRating+"/5");
                    adapter = new ReviewAdapter(filteredReviewsList);
                    recyclerView.setAdapter(adapter);
                    Log.d("API", "Số lượng đánh giá: " + reviews.size());
                } else {
                    Log.e("API", "Lỗi phản hồi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ReviewModel>> call, Throwable t) {
                Log.e("API", "Lỗi kết nối", t);
            }
        });
    }
    private void setupTabListeners() {
        tabAllReviews.setOnClickListener(v -> {
            updateTabSelection(tabAllReviews);
            filteredReviewsList.clear();
            filteredReviewsList.addAll(allReviewsList);
            float averageRating = getAverageRating(filteredReviewsList);
            Log.d("Average", "Rating trung bình: " + averageRating);
            ratingBar = findViewById(R.id.rating_bar);
            ratingBar.setRating(averageRating);
            ratingText = findViewById(R.id.tv_rating_value);
            ratingText.setText(averageRating+"/5");
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
            float averageRating = getAverageRating(filteredReviewsList);
            Log.d("Average", "Rating trung bình: " + averageRating);
            ratingBar = findViewById(R.id.rating_bar);
            ratingBar.setRating(averageRating);
            ratingText = findViewById(R.id.tv_rating_value);
            ratingText.setText(averageRating+"/5");
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
            float averageRating = getAverageRating(filteredReviewsList);
            Log.d("Average", "Rating trung bình: " + averageRating);
            ratingBar = findViewById(R.id.rating_bar);
            ratingBar.setRating(averageRating);
            ratingText = findViewById(R.id.tv_rating_value);
            ratingText.setText(averageRating+"/5");
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

    private Uri selectedImageUri;
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    hasSelectedPhoto = true;
                    textAlertHasImage.setVisibility(VISIBLE);
                }
            });

    private void setupCommentSubmission() {
        btnAddPhoto.setOnClickListener(v -> {
            imagePickerLauncher.launch("image/*");
        });

        btnSubmitComment.setOnClickListener(v -> {
            String commentText = editComment.getText().toString().trim();
            String ratingText = getRatingText(userRating);
            if (commentText.isEmpty()) {
                Toast.makeText(ReviewActivity.this, "Please write a comment", Toast.LENGTH_SHORT).show();
                return;
            }

            // Log the current rating value
            Log.d(TAG, "Submitting comment with rating: " + userRating);

            // Create new review
            ReviewModel newReview = new ReviewModel();
            newReview.setProductId(productId);
            newReview.setUserId(((MyApplication) getApplication()).getUserInfo().getId());
            newReview.setRating(userRating); // Use the rating from the listener
            newReview.setRatingText(getRatingText(userRating));
            newReview.setContent(commentText);
            newReview.setRating(userRating);

            // Get current date and time
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String currentDateTime = sdf.format(new Date());
            newReview.setDate(currentDateTime);

            // Set image if added
            newReview.setHasImage(hasSelectedPhoto);
//            if (hasSelectedPhoto) {
//                newReview.setImageResourceId(android.R.drawable.ic_menu_gallery);
//            }
            createReview(newReview);
            // Add to lists
//            allReviewsList.add(0, newReview);

            Toast.makeText(ReviewActivity.this, "Đã tải đánh giá", Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadImage(long reviewId, ReviewModel newReview) {
        try {
            ContentResolver contentResolver = getContentResolver();
            String fileName = getFileName(selectedImageUri);
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(selectedImageUri)), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", fileName, requestFile);

            reviewApiService.uploadImage((int) reviewId, body).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
//                        updateUI(newReview);
                        reloadUi();
                    } else {

                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        } catch (IOException e) {
            Log.w("UPLOAD_IMAGE", e);
            Toast.makeText(this, "Error reading image", Toast.LENGTH_SHORT).show();
        }
    }

    private void reloadUi() {
        finish();
        overridePendingTransition( 0, 0);
        startActivity(getIntent());
        overridePendingTransition( 0, 0);
    }

    private void updateUI(ReviewModel newReview) {
        allReviewsList.add(0, newReview); // Thêm review mới vào đầu danh sách
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
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
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
//        review1.setUsername("Lycoon");
        review1.setRating(4);
        review1.setRatingText("Đánh giá: Tốt");
        review1.setContent("Hàng vip nha mọi người");
        review1.setDate("15/3/2025 05:54");
        review1.setHasImage(false);
        allReviewsList.add(review1);

        // Second review
        ReviewModel review2 = new ReviewModel();
//        review2.setUsername("Rina");
        review2.setRating(4);
        review2.setRatingText("Đánh giá: Tạm tạm");
        review2.setContent("Không như quảng cáo");
        review2.setDate("10/3/2025 16:20");
        review2.setHasImage(true);
        review2.setImageResourceId(R.drawable.couch);
        allReviewsList.add(review2);

        // Add more sample reviews with different ratings for testing
        ReviewModel review3 = new ReviewModel();
//        review3.setUsername("Alex");
        review3.setRating(5);
        review3.setRatingText("Đánh giá: Tốt");
        review3.setContent("Sản phẩm tuyệt vời!");
        review3.setDate("08/3/2025 10:15");
        review3.setHasImage(true);
        review3.setImageResourceId(R.drawable.couch);
        allReviewsList.add(review3);

        ReviewModel review4 = new ReviewModel();
//        review4.setUsername("Maria");
        review4.setRating(3);
        review4.setRatingText("Đánh giá: Trung bình");
        review4.setContent("Chất lượng ổn, giá hơi cao");
        review4.setDate("05/3/2025 14:30");
        review4.setHasImage(false);
        allReviewsList.add(review4);
    }
}
