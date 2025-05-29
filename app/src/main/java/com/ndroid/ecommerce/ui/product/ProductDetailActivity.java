package com.ndroid.ecommerce.ui.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.ndroid.ecommerce.MyApplication;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.product.ProductAdapter;
import com.ndroid.ecommerce.adapter.product.ProductDetailPagerAdapter;
import com.ndroid.ecommerce.ui.review.ReviewActivity;

import java.util.List;
import java.util.logging.Logger;

import api.order.ApiClient;
import api.order.CartOrderService;
import api.order.entity.CartResponse;
import api.order.entity.UpdateCartRequest;
import api.product.ProductClient;
import api.product.ProductService;
import api.rating.ReviewApiService;
import api.rating.ReviewClient;
import model.product.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {
    private Product product;
    private int productId;
    private String productDescription;
    private final ReviewApiService reviewApiService = ReviewClient.getRetrofit().create(ReviewApiService.class);
    private final CartOrderService cartOrderService = ApiClient.getClient().create(CartOrderService.class);
    private final ProductService productService = ProductClient.getRetrofit().create(ProductService.class);
    private CardView review;
    private TextView name, price, reviewStarCount, reviewTotalCount, productCount;
    private ImageButton btnDecrease, btnIncrease;
    private Button btnAddToBag;
    private RecyclerView recViewInterested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        product = (Product) getIntent().getSerializableExtra("product");
        productId = getIntent().getIntExtra("product_id", 1);
        productDescription = getIntent().getStringExtra("product_description");

        name = findViewById(R.id.tvName);
        name.setText(product.getProductName());
        price = findViewById(R.id.tvPrice);
        price.setText(String.format("%,dđ", product.getProductCost()));
        setStarCountTv();
        setTotalCountTv();

        review = findViewById(R.id.review);
        review.setOnClickListener(l -> {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewActivity.class);
            intent.putExtra("product_id", productId);
            startActivity(intent);
        });

        // Set up ViewPager2 and TabLayout
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        ProductDetailPagerAdapter adapter = new ProductDetailPagerAdapter(this, productId, productDescription);
        viewPager.setAdapter(adapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Mô tả");
                    break;
                case 1:
                    tab.setText("Cửa hàng");
                    break;
            }
        }).attach();
        ImageView imageView = findViewById(R.id.productImage);
        Glide.with(this)
                .load(ProductClient.PRODUCT_ENDPOINT_URL + "product/image/" + productId)
                .placeholder(R.drawable.baseline_photo_camera_24)
                .error(R.drawable.baseline_photo_camera_24)
                .into(imageView);

        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        productCount = findViewById(R.id.tvCount);

        btnDecrease.setOnClickListener(l -> decreaseProductCount());
        btnIncrease.setOnClickListener(l -> increaseProductCount());

        btnAddToBag = findViewById(R.id.btnAddToBag);
        btnAddToBag.setOnClickListener(l -> addToBag());

        recViewInterested = findViewById(R.id.recyclerViewInterested);
        recViewInterested.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadRecommendation(productService);

        ImageButton buttonBack = findViewById(R.id.btnBack);
        buttonBack.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStarCountTv();
        setTotalCountTv();
    }

    private void setTotalCountTv() {
        reviewTotalCount = findViewById(R.id.reviewTotalCount);
        reviewApiService.getRatingCount(productId).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                if (response.isSuccessful()) {
                    reviewTotalCount.setText(response.body() + " đánh giá >>>");
                } else {
                    reviewTotalCount.setText("ERROR");
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable throwable) {
                reviewTotalCount.setText("ERROR CONNECTION");
            }
        });
    }

    private void setStarCountTv() {
        reviewStarCount = findViewById(R.id.reviewStarCount);
        RatingBar ratingBar = findViewById(R.id.ratingBar);
        reviewApiService.getAverageRating(productId).enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful()) {
                    reviewStarCount.setText(String.valueOf(response.body()));
                    ratingBar.setRating((float) response.body().doubleValue());
                } else {
                    reviewStarCount.setText("ERROR");
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable throwable) {
                reviewStarCount.setText("ERROR CONNECTION");
            }
        });
    }

    private void increaseProductCount() {
        String count = productCount.getText().toString();
        String newCount = String.valueOf(Integer.parseInt(count) + 1);
        productCount.setText(newCount);
    }

    private void decreaseProductCount() {
        String count = productCount.getText().toString();
        int countInt = Integer.parseInt(count);
        if (countInt <= 1) return;
        String newCount = String.valueOf(countInt - 1);
        productCount.setText(newCount);
    }

    private void loadRecommendation(ProductService productService) {
        productService.getRandomProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    ProductAdapter productAdapter = new ProductAdapter(ProductDetailActivity.this, response.body(), product -> openDetailActivity(product));
                    recViewInterested.setAdapter(productAdapter);
                    Logger.getLogger("INIT_POPULAR").warning(String.valueOf(response.body().size()));
                } else {
                    Toast.makeText(ProductDetailActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Logger.getLogger("INIT_POPULAR").warning(t.getMessage());
            }
        });
    }

    private void openDetailActivity(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("product", product);
        intent.putExtra("product_id", product.getProductId());
        intent.putExtra("product_description", product.getProductDescription());
        startActivity(intent);
    }

    private void addToBag() {
        String userId = String.valueOf(((MyApplication) getApplication()).getUserInfo().getId());
        UpdateCartRequest request = new UpdateCartRequest(userId, productId, Integer.parseInt(productCount.getText().toString()));

        cartOrderService.addToCart(request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful()) {
                    btnAddToBag.setEnabled(false);
                    Toast.makeText(ProductDetailActivity.this, "Thêm hàng vào giỏ thành công!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProductDetailActivity.this, "ERROR "+response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable throwable) {
                Toast.makeText(ProductDetailActivity.this, "ERROR CONNECTION", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
