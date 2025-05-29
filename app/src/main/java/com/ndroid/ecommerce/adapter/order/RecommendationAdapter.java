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

import com.ndroid.ecommerce.R;

import java.util.List;

import api.order.entity.RecommendationItem;

;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.RecommendationViewHolder> {

    private Context context;
    private List<RecommendationItem> recommendations;
    private RecommendationListener listener;

    public interface RecommendationListener {
        void onFavoriteToggle(RecommendationItem item);
        void onItemClick(RecommendationItem item);
    }

    public RecommendationAdapter(Context context, List<RecommendationItem> recommendations, RecommendationListener listener) {
        this.context = context;
        this.recommendations = recommendations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecommendationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommendation, parent, false);
        return new RecommendationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendationViewHolder holder, int position) {
        RecommendationItem item = recommendations.get(position);

        holder.tvRecommendationName.setText(item.getName());
        holder.tvRecommendationPrice.setText(String.format("$%d", (int)item.getPrice()));
        holder.ivRecommendation.setImageResource(item.getImageResource());

        // Set favorite icon based on item state
        holder.btnFavorite.setImageResource(
                item.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
        );

        holder.btnFavorite.setOnClickListener(v -> {
            item.setFavorite(!item.isFavorite());
            holder.btnFavorite.setImageResource(
                    item.isFavorite() ? R.drawable.ic_favorite : R.drawable.ic_favorite_border
            );
            listener.onFavoriteToggle(item);
        });

        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    static class RecommendationViewHolder extends RecyclerView.ViewHolder {
        ImageView ivRecommendation;
        TextView tvRecommendationName;
        TextView tvRecommendationPrice;
        ImageButton btnFavorite;

        public RecommendationViewHolder(@NonNull View itemView) {
            super(itemView);
            ivRecommendation = itemView.findViewById(R.id.ivRecommendation);
            tvRecommendationName = itemView.findViewById(R.id.tvRecommendationName);
            tvRecommendationPrice = itemView.findViewById(R.id.tvRecommendationPrice);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}
