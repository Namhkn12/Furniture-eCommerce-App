package com.ndroid.ecommerce.adapter.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.R;

import java.util.ArrayList;
import java.util.List;

import model.product.Selling;
import model.product.Shop;

public class SellingAdapter extends RecyclerView.Adapter<SellingAdapter.ViewHolder> {
    private List<Selling> sellings = new ArrayList<>();

    public SellingAdapter(List<Selling> sellings) {
        this.sellings.addAll(sellings);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Selling selling = sellings.get(position);
        Shop shop = selling.getShop();
        holder.name.setText(shop.getShopName());
        holder.address.setText(shop.getShopLocation());
        holder.phoneNumber.setText(shop.getShopPhoneNumber());
        holder.stock.setText("Số hàng hiện có: " + selling.getAmount());
    }

    @Override
    public int getItemCount() {
        return sellings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber, address, stock;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameTv);
            phoneNumber = itemView.findViewById(R.id.phoneNumberTv);
            address = itemView.findViewById(R.id.tvAddress);
            stock = itemView.findViewById(R.id.tvStock);
        }
    }
}
