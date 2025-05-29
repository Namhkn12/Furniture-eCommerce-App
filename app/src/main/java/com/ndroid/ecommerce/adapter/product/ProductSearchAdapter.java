package com.ndroid.ecommerce.adapter.product;

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

import java.util.ArrayList;
import java.util.List;

import api.product.ProductClient;
import model.product.Product;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.ViewHolder>{
    private final Context context;
    private List<Product> productList = new ArrayList<>();
    private ProductAdapter.OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductSearchAdapter(Context context, List<Product> products, ProductAdapter.OnProductClickListener listener) {
        this.context = context;
        this.productList.addAll(products);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvProductName.setText(product.getProductName());
        holder.tvPrice.setText(String.format("%,dđ", product.getProductCost()));
        holder.tvDes.setText(product.getProductDescription());

        // Load ảnh sản phẩm

        Glide.with(context)
                .load(ProductClient.PRODUCT_ENDPOINT_URL + "product/image/" + product.getProductId())
                .placeholder(R.drawable.baseline_photo_camera_24)
                .error(R.drawable.baseline_photo_camera_24)
                .into(holder.imgProduct);

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductName, tvPrice, tvDes;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvDes = itemView.findViewById(R.id.tvDescription);
        }
    }
}
