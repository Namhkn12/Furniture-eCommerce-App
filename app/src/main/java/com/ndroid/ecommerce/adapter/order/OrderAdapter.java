package com.ndroid.ecommerce.adapter.order;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.R;

import java.util.List;

import api.order.entity.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orders;
    private OrderListener listener;

    public interface OrderListener {
        void onTrackOrder(Order order);
        void onReorder(Order order);
    }

    public OrderAdapter(Context context, List<Order> orders, OrderListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.getId());
        holder.tvOrderDate.setText(order.getDate());
        holder.tvOrderTotal.setText(String.format("%,.0fđ", order.getTotal()));

        // Set status with appropriate color
        holder.tvOrderStatus.setText(order.getStatus());
        switch (order.getStatus().toLowerCase()) {
            case "delivered":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "processing":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.orange));
                break;
            case "shipped":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.blue));
                break;
            case "cancelled":
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.red));
                break;
            default:
                holder.tvOrderStatus.setTextColor(context.getResources().getColor(R.color.gray));
                break;
        }

        // Setup order items recycler view
        OrderProductAdapter productAdapter = new OrderProductAdapter(context, order.getProducts());
        holder.recyclerOrderItems.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerOrderItems.setAdapter(productAdapter);

        // Setup button listeners
        holder.btnTrackOrder.setOnClickListener(v -> {
            listener.onTrackOrder(order);
        });

        holder.btnReorder.setOnClickListener(v -> {
            listener.onReorder(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders.clear();
        this.orders.addAll(newOrders);
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvOrderDate;
        TextView tvOrderStatus;
        TextView tvOrderTotal;
        RecyclerView recyclerOrderItems;
        Button btnTrackOrder;
        Button btnReorder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvOrderTotal = itemView.findViewById(R.id.tvOrderTotal);
            recyclerOrderItems = itemView.findViewById(R.id.recyclerOrderItems);
            btnTrackOrder = itemView.findViewById(R.id.btnTrackOrder);
            btnReorder = itemView.findViewById(R.id.btnReorder);
        }
    }
}
