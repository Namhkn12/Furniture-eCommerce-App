package com.ndroid.ecommerce.adapter.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.ndroid.ecommerce.ui.product.fragment.DescriptionFragment;
import com.ndroid.ecommerce.ui.product.fragment.ShopFragment;

public class ProductDetailPagerAdapter extends FragmentStateAdapter {

    private int productId;
    private String productDescription;

    public ProductDetailPagerAdapter(@NonNull FragmentActivity fragmentActivity, int productId, String productDescription) {
        super(fragmentActivity);
        this.productId = productId;
        this.productDescription = productDescription;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return ShopFragment.newInstance(productId);
            default:
                return DescriptionFragment.newInstance(productDescription);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
