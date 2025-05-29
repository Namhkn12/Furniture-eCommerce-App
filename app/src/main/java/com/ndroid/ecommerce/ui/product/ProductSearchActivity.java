package com.ndroid.ecommerce.ui.product;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.product.ProductSearchAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import api.product.ProductClient;
import api.product.ProductService;
import model.product.Category;
import model.product.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductSearchActivity extends AppCompatActivity {

    private final ProductService productService = ProductClient.getRetrofit().create(ProductService.class);
    private EditText etSearchKeyword;
    private Button btnSelectCategories, btnSearch;
    private RecyclerView recyclerView;
    private TextView tvSelectedCategories, noProduct;
    private ImageView btnBack;
    private List<Category> categories = new ArrayList<>();
    private boolean[] selected;
    private ArrayList<Integer> selectedCategoryIds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_search);
        btnBack = findViewById(R.id.backButton);
        etSearchKeyword = findViewById(R.id.etSearchKeyword);
        btnSelectCategories = findViewById(R.id.btnSelectCategories);
        btnSearch = findViewById(R.id.btnSearch);
        tvSelectedCategories = findViewById(R.id.tvSelectedCategories);
        noProduct = findViewById(R.id.tvNoItem);

        recyclerView = findViewById(R.id.recViewProduct);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        String searchQuery = getIntent().getStringExtra("search_query");
        if (searchQuery != null) {
            etSearchKeyword.setText(searchQuery);
            searchAndProduct(searchQuery);
        }

        categories.add(new Category(1, "Ghế"));
        categories.add(new Category(2, "Bàn"));
        categories.add(new Category(3, "Giường"));
        categories.add(new Category(4, "Tủ"));

        selected = new boolean[categories.size()];
        int categoryId = getIntent().getIntExtra("category_id", 0);
        if (categoryId != 0) {
            selected[categoryId - 1] = true;
            selectedCategoryIds.add(categoryId);
            searchAndProduct("");

            List<String> selectedNames = selectedCategoryIds.stream()
                    .map(id -> categories.stream()
                            .filter(cat -> cat.getCategoryId() == id)
                            .map(Category::getCategoryName)
                            .findFirst().orElse("")).collect(Collectors.toList());

            tvSelectedCategories.setText("Đã chọn: " + String.join(", ", selectedNames));
        }

        btnSelectCategories.setOnClickListener(v -> {
            showCategoryDialog();
        });

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearchKeyword.getText().toString().trim();
            searchAndProduct(keyword);
        });

        btnBack.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void showCategoryDialog() {
        String[] categoryNames = categories.stream()
                .map(Category::getCategoryName)
                .toArray(String[]::new);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn loại nội thất");

        builder.setMultiChoiceItems(categoryNames, selected, (dialog, which, isChecked) -> {
            int categoryId = categories.get(which).getCategoryId();
            if (isChecked) {
                if (!selectedCategoryIds.contains(categoryId))
                    selectedCategoryIds.add(categoryId);
            } else {
                selectedCategoryIds.remove((Integer) categoryId);
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            List<String> selectedNames = selectedCategoryIds.stream()
                    .map(id -> categories.stream()
                            .filter(cat -> cat.getCategoryId() == id)
                            .map(Category::getCategoryName)
                            .findFirst().orElse("")).collect(Collectors.toList());

            tvSelectedCategories.setText("Đã chọn: " + String.join(", ", selectedNames));
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void searchAndProduct(String keyword) {

        productService.searchProductByName(keyword, selectedCategoryIds).enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    if (response.body().isEmpty()) {
                        noProduct.setVisibility(VISIBLE);
                        return;
                    }
                    noProduct.setVisibility(INVISIBLE);
                    ProductSearchAdapter productAdapter = new ProductSearchAdapter(ProductSearchActivity.this, response.body(), product -> openDetailActivity(product));
                    recyclerView.setAdapter(productAdapter);
                } else {
                    Toast.makeText(ProductSearchActivity.this, "Search failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(ProductSearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
}