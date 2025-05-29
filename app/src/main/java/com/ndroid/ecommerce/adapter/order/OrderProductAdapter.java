package com.ndroid.ecommerce.adapter.order;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ndroid.ecommerce.R;

import java.util.List;

import api.order.entity.OrderProduct;
import api.product.ProductClient;

public class OrderProductAdapter extends RecyclerView.Adapter<OrderProductAdapter.OrderProductViewHolder> {

    private Context context;
    private List<OrderProduct> products;

    public OrderProductAdapter(Context context, List<OrderProduct> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public OrderProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order_product, parent, false);
        return new OrderProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductViewHolder holder, int position) {
        OrderProduct product = products.get(position);

        holder.tvOrderProductName.setText(product.getName());
        holder.tvOrderProductQuantity.setText("Số lượng: " + product.getQuantity());
        holder.tvOrderProductPrice.setText(String.format("%,.0fđ", product.getPrice()));
//        holder.ivOrderProduct.setImageResource(Integer.parseInt(product.getImageResource()));
        Glide.with(context)
                .load(ProductClient.PRODUCT_ENDPOINT_URL + "product/image/" + product.getProductId())
                .placeholder(R.drawable.baseline_photo_camera_24)
                .error(R.drawable.baseline_photo_camera_24)
                .into(holder.ivOrderProduct);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class OrderProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOrderProduct;
        TextView tvOrderProductName;
        TextView tvOrderProductQuantity;
        TextView tvOrderProductPrice;

        public OrderProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOrderProduct = itemView.findViewById(R.id.ivOrderProduct);
            tvOrderProductName = itemView.findViewById(R.id.tvOrderProductName);
            tvOrderProductQuantity = itemView.findViewById(R.id.tvOrderProductQuantity);
            tvOrderProductPrice = itemView.findViewById(R.id.tvOrderProductPrice);
        }
    }
}
