package com.ndroid.ecommerce.ui.order;


import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ndroid.ecommerce.MyApplication;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.order.CartAdapter;
import com.ndroid.ecommerce.adapter.product.ProductAdapter;
import com.ndroid.ecommerce.ui.UserDashboardActivity;
import com.ndroid.ecommerce.ui.chat.ChatActivity;
import com.ndroid.ecommerce.ui.product.HomeActivity;
import com.ndroid.ecommerce.ui.product.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import api.order.ApiClient;
import api.order.CartOrderService;
import api.order.entity.CartItem;
import api.order.entity.CartItemModel;
import api.order.entity.CartResponse;
import api.order.entity.OrderRequest;
import api.order.entity.OrderResponse;
import api.order.entity.UpdateCartRequest;
import api.product.ProductClient;
import api.product.ProductService;
import model.product.Product;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShoppingCartActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private RecyclerView recyclerCartItems, recyclerRecommendation;
    private CartAdapter cartAdapter;
    private TextView tvTotalPrice;
    private Button btnCheckout;
    private ImageButton btnBack;
    private Button btnViewOrders;
    private View loadingView;
    private TextView tvEmptyCart;

    private CartOrderService cartOrderService;
    private String userId;
    private List<CartItemModel> currentCartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        // Initialize API service
        cartOrderService = ApiClient.getClient().create(CartOrderService.class);
        ProductService productService = ProductClient.getRetrofit().create(ProductService.class);
        // Get user ID from SharedPreferences (in a real app, this would come from your auth system)
//        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        userId = prefs.getString("userId", "user123"); // Default to "user123" if not found

        userId = String.valueOf(((MyApplication) getApplication()).getUserInfo().getId());

        initViews();
        setupCartRecyclerView();
        setupListeners();

        // Load cart data
        loadCartData();
        //Recommendation
        loadRecommendation(productService);
    }

    private void loadRecommendation(ProductService productService) {
        productService.getRandomProduct().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    ProductAdapter productAdapter = new ProductAdapter(ShoppingCartActivity.this, response.body(), product -> openDetailActivity(product));
                    recyclerRecommendation.setAdapter(productAdapter);
                    Logger.getLogger("INIT_POPULAR").warning(String.valueOf(response.body().size()));
                } else {
                    Toast.makeText(ShoppingCartActivity.this, String.valueOf(response.code()), Toast.LENGTH_SHORT).show();
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

    private void initViews() {
        recyclerRecommendation = findViewById(R.id.recyclerRecommendations);
        recyclerCartItems = findViewById(R.id.recyclerCartItems);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
        btnViewOrders = findViewById(R.id.btnViewOrders);
        loadingView = findViewById(R.id.loadingView);
        tvEmptyCart = findViewById(R.id.tvEmptyCart);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }

    private void setupCartRecyclerView() {
        recyclerCartItems.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, new ArrayList<>(), new CartAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQuantity) {
//                The response body doesn't have a list!
                updateCartItemQuantity(item.getId(), newQuantity);
//                loadCartData();
            }

            @Override
            public void onRemoveItem(CartItem item) {
                removeCartItem(item.getId());
            }
        });
        recyclerCartItems.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCheckout();
            }
        });

        btnViewOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to Orders screen
                Intent intent = new Intent(ShoppingCartActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        bottomNavigation.setSelectedItemId(R.id.nav_shop);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(ShoppingCartActivity.this, HomeActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else if (id == R.id.nav_chat) {
                Intent intent1 = new Intent(ShoppingCartActivity.this, ChatActivity.class);
                intent1.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent1);
            } else if (id == R.id.nav_shop) {
                return true;
            } else if (id == R.id.nav_profile) {
                Intent intent3 = new Intent(ShoppingCartActivity.this, UserDashboardActivity.class);
                intent3.setFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent3);
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        bottomNavigation.setSelectedItemId(R.id.nav_shop);
        userId = String.valueOf(((MyApplication) getApplication()).getUserInfo().getId());
        loadCartData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        overridePendingTransition(0, 0);
    }

    private void loadCartData() {
        showLoading(true);

        cartOrderService.getCart(userId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    CartResponse cartResponse = response.body();
                    currentCartItems = cartResponse.getItems();
                    updateCartUI(cartResponse);
                } else {
                    showError("Failed to load cart data");
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void updateCartUI(CartResponse cartResponse) {
        List<CartItemModel> cartItems = cartResponse.getItems();

        if (cartItems == null || cartItems.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            recyclerCartItems.setVisibility(View.GONE);
            btnCheckout.setEnabled(false);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            recyclerCartItems.setVisibility(View.VISIBLE);
            btnCheckout.setEnabled(true);

            // Convert API models to UI models
            List<CartItem> uiCartItems = new ArrayList<>();
            for (CartItemModel item : cartItems) {
                uiCartItems.add(new CartItem(
                        item.getId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity(),
                        item.getImageUrl()
                ));
            }

            cartAdapter.updateCartItems(uiCartItems);
        }

        // Update total price
        tvTotalPrice.setText(String.format("%,.0fđ", cartResponse.getTotal()));
    }

    private void updateCartItemQuantity(int itemId, int newQuantity) {
        UpdateCartRequest request = new UpdateCartRequest(userId, itemId, newQuantity);

        cartOrderService.updateCartItem(itemId, request).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
//                    updateCartUI(response.body());
                    loadCartData();
                } else {
                    showError("Failed to update item quantity");
                    // Reload cart to ensure UI is in sync with server
                    loadCartData();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                loadCartData();
            }
        });
    }

    private void removeCartItem(int itemId) {
        cartOrderService.removeCartItem(itemId).enqueue(new Callback<CartResponse>() {
            @Override
            public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loadCartData();
                    Toast.makeText(ShoppingCartActivity.this, "Item removed", Toast.LENGTH_SHORT).show();
                } else {
                    showError("Failed to remove item");
                    loadCartData();
                }
            }

            @Override
            public void onFailure(Call<CartResponse> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                loadCartData();
            }
        });
    }

    private void clearCart() {
        for (CartItemModel cartItemModel : currentCartItems) {
            cartOrderService.removeCartItem(cartItemModel.getId()).enqueue(new Callback<CartResponse>() {
                @Override
                public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                    } else {
                        showError("Failed to remove item");
                    }
                }

                @Override
                public void onFailure(Call<CartResponse> call, Throwable t) {
                    showError("Network error: " + t.getMessage());
                }
            });
        }
    }

    private void navigateToCheckout() {
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            showError("Your cart is empty");
            return;
        }
        // Launch CheckoutActivity to collect shipping and payment details
        Intent intent = new Intent(ShoppingCartActivity.this, CheckoutActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String shippingAddress = data.getStringExtra("shipping_address");
            String paymentMethod = data.getStringExtra("payment_method");
            createOrder(shippingAddress, paymentMethod);
        }
    }

    private void createOrder(String shippingAddress, String paymentMethod) {
        if (currentCartItems == null || currentCartItems.isEmpty()) {
            showError("Your cart is empty");
            return;
        }

        showLoading(true);

        OrderRequest orderRequest = new OrderRequest(userId, currentCartItems, shippingAddress, paymentMethod);

        cartOrderService.createOrder(orderRequest).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    // Order created successfully
                    Toast.makeText(ShoppingCartActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                    clearCart();
                    // Navigate to order history
                    Intent intent = new Intent(ShoppingCartActivity.this, OrderActivity.class);
                    startActivity(intent);
                    finish(); // Close cart activity
                } else {
                    showError("Failed to place order");
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean isLoading) {
        loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}