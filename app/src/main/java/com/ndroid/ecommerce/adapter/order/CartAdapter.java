package com.ndroid.ecommerce.adapter.order;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ndroid.ecommerce.R;

import java.util.List;

import api.order.entity.CartItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onRemoveItem(CartItem item);
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.tvProductName.setText(item.getName());
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
        holder.tvQuantityValue.setText(String.valueOf(item.getQuantity()));
        holder.tvPrice.setText(String.format("%,.0fđ", item.getPrice()));

        // Load image using Glide
        if (item.getImageResource() != null && !item.getImageResource().isEmpty()) {
//            Glide.with(context)
//                    .load(item.getImageResource())
//                    .placeholder(R.drawable.placeholder_image)
//                    .into(holder.ivProduct);
            Glide.with(context)
                    .load(item.getImageResource())
                    .placeholder(R.drawable.baseline_photo_camera_24)
                    .error(R.drawable.baseline_photo_camera_24)
                    .into(holder.ivProduct);
        } else {
            holder.ivProduct.setImageResource(Integer.parseInt(item.getImageResource()));
        }

        holder.btnDecrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity >= 1) {
                item.setQuantity(newQuantity);
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                holder.tvQuantityValue.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged(item, newQuantity);
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            item.setQuantity(newQuantity);
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            holder.tvQuantityValue.setText(String.valueOf(newQuantity));
            listener.onQuantityChanged(item, newQuantity);
        });

        holder.ivRemove.setOnClickListener(v -> {
            listener.onRemoveItem(item);
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void updateCartItems(List<CartItem> newItems) {
        this.cartItems.clear();
        this.cartItems.addAll(newItems);
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvProductName;
        TextView tvQuantity;
        TextView tvQuantityValue;
        TextView tvPrice;
        ImageButton btnDecrease;
        ImageButton btnIncrease;
        ImageButton ivRemove;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvQuantityValue = itemView.findViewById(R.id.tvQuantityValue);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            ivRemove = itemView.findViewById(R.id.ivRemove);
        }
    }
}

