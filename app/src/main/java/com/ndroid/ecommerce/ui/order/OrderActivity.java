package com.ndroid.ecommerce.ui.order;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.MyApplication;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.order.OrderAdapter;

import java.util.ArrayList;
import java.util.List;

import api.order.ApiClient;
import api.order.CartOrderService;
import api.order.entity.Order;
import api.order.entity.OrderItemModel;
import api.order.entity.OrderListResponse;
import api.order.entity.OrderModel;
import api.order.entity.OrderProduct;
import api.order.entity.UserResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    private RecyclerView recyclerOrders;
    private TextView tvEmptyOrders;
    private OrderAdapter orderAdapter;
    private ImageView btnBack;
    private View loadingView;

    private CartOrderService cartOrderService;
    private String userId;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // Initialize API service
        cartOrderService = ApiClient.getClient().create(CartOrderService.class);

//        // Get user ID from SharedPreferences (in a real app, this would come from your auth system)
//        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
//        userId = prefs.getString("userId", "user123"); // Default to "user123" if not found

        userId = String.valueOf(((MyApplication) getApplication()).getUserInfo().getId());

        initViews();
        setupOrdersRecyclerView();
        setupListeners();

//        No need for userInfo since we only need id
//        getUserInfo();
        loadOrdersData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = String.valueOf(((MyApplication) getApplication()).getUserInfo().getId());
    }

    private void initViews() {
        recyclerOrders = findViewById(R.id.recyclerOrders);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        btnBack = findViewById(R.id.backButton);
        loadingView = findViewById(R.id.loadingView);
    }

    private void setupOrdersRecyclerView() {
        recyclerOrders.setLayoutManager(new LinearLayoutManager(this));

        orderAdapter = new OrderAdapter(this, new ArrayList<>(), new OrderAdapter.OrderListener() {
            @Override
            public void onTrackOrder(Order order) {
                // Track order logic would go here
                Toast.makeText(OrderActivity.this, "Tracking order #" + order.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReorder(Order order) {
                // Reorder logic would go here
                Toast.makeText(OrderActivity.this, "Reordering items from order #" + order.getId(), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerOrders.setAdapter(orderAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserInfo() {
        showLoading(true);

        cartOrderService.getUserInfo(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    userName = userResponse.getName();

                    // Now load the orders
                    loadOrdersData();
                } else {
                    showLoading(false);
                    showError("Failed to load user information");
                    // Still try to load orders even if user info fails
                    loadOrdersData();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                // Still try to load orders even if user info fails
                loadOrdersData();
            }
        });
    }

    private void loadOrdersData() {
        showLoading(true);

        cartOrderService.getOrderHistory(userId).enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<OrderModel> orderModels = response.body().getOrders();
                    updateOrdersUI(orderModels);
                } else {
                    showError("Failed to load orders");
                    tvEmptyOrders.setVisibility(View.VISIBLE);
                    recyclerOrders.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                showLoading(false);
                showError("Network error: " + t.getMessage());
                tvEmptyOrders.setVisibility(View.VISIBLE);
                recyclerOrders.setVisibility(View.GONE);
            }
        });
    }

    private void updateOrdersUI(List<OrderModel> orderModels) {
        if (orderModels == null || orderModels.isEmpty()) {
            tvEmptyOrders.setVisibility(View.VISIBLE);
            recyclerOrders.setVisibility(View.GONE);
        } else {
            tvEmptyOrders.setVisibility(View.GONE);
            recyclerOrders.setVisibility(View.VISIBLE);

            // Convert API models to UI models
            List<Order> orders = new ArrayList<>();
            for (OrderModel orderModel : orderModels) {
                // Convert order items
                List<OrderProduct> products = new ArrayList<>();
                for (OrderItemModel item : orderModel.getItems()) {
                    products.add(new OrderProduct(
                            item.getId(),
                            item.getProductId(),
                            item.getName(),
                            item.getPrice(),
                            item.getQuantity(),
                            item.getImageUrl()
                    ));
                }

                orders.add(new Order(
                        orderModel.getId(),
                        orderModel.getOrderDate(),
                        orderModel.getStatus(),
                        products,
                        orderModel.getTotal()
                ));
            }

            orderAdapter.updateOrders(orders);
        }
    }

    private void showLoading(boolean isLoading) {
        loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

