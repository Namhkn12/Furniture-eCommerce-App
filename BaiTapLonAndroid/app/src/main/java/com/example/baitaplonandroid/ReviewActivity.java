package com.example.baitaplonandroid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ReviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ReviewAdapter adapter;
    private List<ReviewModel> reviewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recycler_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create sample data
        createSampleData();

        // Set up adapter
        adapter = new ReviewAdapter(reviewList);
        recyclerView.setAdapter(adapter);
    }

    private void createSampleData() {
        reviewList = new ArrayList<>();

        // First review
        ReviewModel review1 = new ReviewModel();
        review1.setUsername("Lycoon");
        review1.setRating(4);
        review1.setRatingText("Đánh giá: Tốt");
        review1.setContent("Hàng vip nha mọi người");
        review1.setDate("15/3/2025 05:54");
        review1.setHasImage(false);
        reviewList.add(review1);

        // Second review
        ReviewModel review2 = new ReviewModel();
        review2.setUsername("Rina");
        review2.setRating(4);
        review2.setRatingText("Đánh giá: Tạm tạm");
        review2.setContent("Không như quảng cáo");
        review2.setDate("10/3/2025 16:20");
        review2.setHasImage(true);
        review2.setImageResourceId(R.drawable.couch); // You'll need to add this image to your drawable resources
        reviewList.add(review2);
    }
}