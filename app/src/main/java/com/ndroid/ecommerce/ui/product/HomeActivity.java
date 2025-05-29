package com.ndroid.ecommerce.ui.product;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.product.ProductAdapter;
import com.ndroid.ecommerce.databinding.ActivityHomeBinding;
import com.ndroid.ecommerce.ui.UserDashboardActivity;
import com.ndroid.ecommerce.ui.chat.ChatActivity;
import com.ndroid.ecommerce.ui.order.ShoppingCartActivity;

import java.util.List;
import java.util.logging.Logger;

import api.product.ProductClient;
import api.product.ProductService;
import model.product.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    private ProductService productService = ProductClient.getRetrofit().create(ProductService.class);
    private int[] imageResIds = {
            R.drawable.banner1,
            R.drawable.banner2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.recViewPopular.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false));
        initPopular();
        binding.searchIcon.setOnClickListener(l -> {
            String query = binding.editTextSearch.getText().toString().trim();
            if (!query.isBlank()) {
                openSearchActivity(query);
            }
        });
        binding.editTextSearch.setOnEditorActionListener((l, a, x) -> {
            String query = binding.editTextSearch.getText().toString().trim();
            if (!query.isBlank()) {
                openSearchActivity(query);
            }
            return true;
        });

        binding.btnChair.setOnClickListener(l -> openSearchActivity(1));
        binding.btnTable.setOnClickListener(l -> openSearchActivity(2));
        binding.btnBed.setOnClickListener(l -> openSearchActivity(3));
        binding.btnCabinet.setOnClickListener(l -> openSearchActivity(4));
        binding.banner.setOnClickListener(l -> openSearchActivity(""));

        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_chat) {
                Intent intent1 = new Intent(HomeActivity.this, ChatActivity.class);
                intent1.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent1);
            } else if (id == R.id.nav_shop) {
                Intent intent2 = new Intent(HomeActivity.this, ShoppingCartActivity.class);
                intent2.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent2);
            } else if (id == R.id.nav_profile) {
                Intent intent3 = new Intent(HomeActivity.this, UserDashboardActivity.class);
                intent3.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
            }
            return true;
        });

        startImageLoop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
    }

    private void initPopular() {
        productService.getRandomProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    ProductAdapter productAdapter = new ProductAdapter(HomeActivity.this, response.body(), product -> openDetailActivity(product));
                    binding.recViewPopular.setAdapter(productAdapter);
                    Logger.getLogger("INIT_POPULAR").warning(String.valueOf(response.body().size()));
                } else {
                    Toast.makeText(HomeActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
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

    private void openSearchActivity(String query) {
        Intent intent = new Intent(this, ProductSearchActivity.class);
        intent.putExtra("search_query", query);
        startActivity(intent);
    }

    private void openSearchActivity(int categoryId) {
        Intent intent = new Intent(this, ProductSearchActivity.class);
        intent.putExtra("category_id", categoryId);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopImageLoop();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startImageLoop();
    }

    private int currentIndex = 0;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int interval = 3000;
    private Runnable animation = new Runnable() {
        @Override
        public void run() {
            Glide.with(HomeActivity.this)
                    .load(imageResIds[currentIndex])
                    .transition(DrawableTransitionOptions.withCrossFade(1000))
                    .into(binding.banner);

            currentIndex = (currentIndex + 1) % imageResIds.length;

            handler.postDelayed(this, interval);
        }
    };
    private void startImageLoop() {
        handler.postDelayed(animation, interval);
    }

    private void stopImageLoop() {
        handler.removeCallbacks(animation);
    }
}