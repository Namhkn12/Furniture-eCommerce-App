package com.ndroid.ecommerce.ui.product.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.adapter.product.SellingAdapter;

import java.util.List;
import java.util.logging.Logger;

import api.product.ProductClient;
import api.product.ProductService;
import model.product.Selling;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShopFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShopFragment extends Fragment {
    private static final String PRODUCT_ID = "product_id";
    private int productId;
    private RecyclerView recyclerView;

    private static final ProductService productService = ProductClient.getRetrofit().create(ProductService.class);


    public ShopFragment() {
        // Required empty public constructor
    }

    public static ShopFragment newInstance(int productId) {
        ShopFragment fragment = new ShopFragment();
        Bundle args = new Bundle();
        args.putInt(PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            productId = getArguments().getInt(PRODUCT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recViewShopList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        productService.getSellingByProduct(productId).enqueue(new Callback<List<Selling>>() {
            @Override
            public void onResponse(Call<List<Selling>> call, Response<List<Selling>> response) {
                if (response.isSuccessful()) {
                    SellingAdapter sellingAdapter = new SellingAdapter(response.body());
                    recyclerView.setAdapter(sellingAdapter);
                } else {
                    Logger.getLogger("SHOP_FRAGMENT").warning("FAILED " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Selling>> call, Throwable t) {
                Logger.getLogger("SHOP_FRAGMENT").warning(t.getMessage());
            }
        });
    }
}