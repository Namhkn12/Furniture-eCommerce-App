package com.ndroid.ecommerce.ui.setting;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ndroid.ecommerce.adapter.AddressAdapter;
import com.ndroid.ecommerce.databinding.ActivityAccountAddressSettingBinding;
import com.ndroid.ecommerce.util.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import api.user.UserApiClient;
import api.user.UserApiService;
import model.user.UserAddress;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountAddressSettingActivity extends AppCompatActivity {

    private ActivityAccountAddressSettingBinding binding;
    private final UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private List<UserAddress> userAddressList = new ArrayList<>();
    private AddressAdapter adapter;
    private UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAccountAddressSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");

        reloadUserAddress();

        // Add new address button
        binding.addAddressButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddAddressActivity.class);
            intent.putExtra("user_id", userInfo.getId());
            startActivityForResult(intent, 101);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        reloadUserAddress();
    }

    private void reloadUserAddress() {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<List<UserAddress>> call = userApiService.getAddressesOfUser(userInfo.getId());
        call.enqueue(new Callback<List<UserAddress>>() {
            @Override
            public void onResponse(Call<List<UserAddress>> call, Response<List<UserAddress>> response) {
                if (response.isSuccessful()) {
                    loadingDialog.hide();
                    userAddressList.clear();
                    userAddressList.addAll(response.body());
                    adapter = new AddressAdapter(userAddressList, position -> {
                        Intent intent = new Intent(AccountAddressSettingActivity.this, EditAddressActivity.class);
                        intent.putExtra("index", position);
                        UserAddress userAddress = userAddressList.get(position);
                        intent.putExtra("id", userAddress.getId());
                        intent.putExtra("name", userAddress.getName());
                        intent.putExtra("phone", userAddress.getPhoneNumber());
                        intent.putExtra("road", userAddress.getRoad());
                        intent.putExtra("city", userAddress.getCity());
                        startActivityForResult(intent, 100);
                    });
                    binding.recyclerView.setAdapter(adapter);
                } else {
                    loadingDialog.hide();
                    Toast.makeText(AccountAddressSettingActivity.this, "SUCCESS BUT ERROR", LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserAddress>> call, Throwable throwable) {
                loadingDialog.hide();
                Toast.makeText(AccountAddressSettingActivity.this, "ERROR", LENGTH_SHORT).show();
            }
        });
    }
}
