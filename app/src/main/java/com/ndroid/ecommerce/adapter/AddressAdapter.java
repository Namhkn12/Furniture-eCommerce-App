package com.ndroid.ecommerce.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.R;

import java.util.ArrayList;
import java.util.List;

import model.user.UserAddress;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {
    private List<UserAddress> userAddressList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public AddressAdapter(List<UserAddress> userAddressList, OnItemClickListener listener) {
        this.userAddressList.addAll(userAddressList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        UserAddress addr = userAddressList.get(position);
        holder.tvName.setText(addr.getName());
        holder.tvPhone.setText(addr.getPhoneNumber());
        holder.tvRoad.setText(addr.getRoad());
        holder.tvCity.setText(addr.getCity());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return userAddressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvRoad, tvCity;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.nameTv);
            tvPhone = itemView.findViewById(R.id.phoneNumberTv);
            tvRoad = itemView.findViewById(R.id.roadTv);
            tvCity = itemView.findViewById(R.id.cityTv);
        }
    }
}

