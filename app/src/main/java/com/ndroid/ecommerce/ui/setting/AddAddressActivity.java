package com.ndroid.ecommerce.ui.setting;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityAddAddressBinding;
import com.ndroid.ecommerce.util.LoadingDialog;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.UserAddressDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    private ActivityAddAddressBinding binding;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
        binding.btnSave.setOnClickListener(l -> addAddress());
        binding.btnCancel.setOnClickListener(l -> finish());

    }

    private void addAddress() {
        int userId = getIntent().getIntExtra("user_id", 0);
        if (userId == 0) {
            Toast.makeText(this, "WHERE ID?", LENGTH_SHORT).show();
            return;
        }
        String newName = binding.editName.getText().toString().trim();
        String newPhone = binding.editPhone.getText().toString().trim();
        String newRoad = binding.editRoad.getText().toString().trim();
        String newCity = binding.editCity.getText().toString().trim();

        if (newName.isBlank() || newPhone.isBlank() || newRoad.isBlank() || newCity.isBlank()) {
            binding.tvWarningNoField.setVisibility(VISIBLE);
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Void> call = userApiService.addAddress(userId, new UserAddressDTO(newName, newCity, newRoad, newPhone));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText(AddAddressActivity.this, "Thêm địa chỉ thành công", LENGTH_SHORT).show();
                    finish();
                } else Toast.makeText(AddAddressActivity.this, response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }
}