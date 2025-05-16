package com.ndroid.ecommerce.ui.setting;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.databinding.ActivityEditAddressBinding;
import com.ndroid.ecommerce.util.LoadingDialog;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.UserAddressDTO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityEditAddressBinding binding;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private GoogleMap gMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAddressBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Receive data
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        String name = intent.getStringExtra("name");
        String phone = intent.getStringExtra("phone");
        String road = intent.getStringExtra("road");
        String city = intent.getStringExtra("city");

        binding.editName.setText(name);
        binding.editPhone.setText(phone);
        binding.editRoad.setText(road);
        binding.editCity.setText(city);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        binding.backButton.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
        binding.btnCancel.setOnClickListener(v -> finish());
        binding.btnSave.setOnClickListener(v -> updateAddress(id));

        binding.btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_confirm_delete_address, null);
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();

            Button btnCancel = dialogView.findViewById(R.id.btnCancel);
            Button btnDelete = dialogView.findViewById(R.id.btnDelete);

            btnCancel.setOnClickListener(l -> dialog.dismiss());
            btnDelete.setOnClickListener(l -> deleteAddress(id));
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        String fullAddress = binding.editRoad.getText().toString() + ", " + binding.editCity.getText().toString();
        Log.w("MAP", fullAddress);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            Geocoder geocoder = new Geocoder(EditAddressActivity.this, Locale.getDefault());
            try {
                List<Address> locations = geocoder.getFromLocationName(fullAddress, 1);
                if (!locations.isEmpty()) {
                    Address loc = locations.get(0);
                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                    handler.post(() -> {
                        gMap.addMarker(new MarkerOptions().position(latLng).title("Địa chỉ"));
                        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    });
                } else {
                    handler.post(() -> Toast.makeText(EditAddressActivity.this, "Không tìm thấy địa chỉ", Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> Toast.makeText(EditAddressActivity.this, "Lỗi khi tìm địa chỉ", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateAddress(int addressId) {
        String newName = binding.editName.getText().toString().trim();
        String newPhone = binding.editPhone.getText().toString().trim();
        String newRoad = binding.editRoad.getText().toString().trim();
        String newCity = binding.editCity.getText().toString().trim();

        if (newName.isBlank() || newPhone.isBlank() || newRoad.isBlank() || newCity.isBlank()) {
            Toast.makeText(this, "Xin hãy nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<String> call = userApiService.updateAddress(addressId, new UserAddressDTO(newName, newCity, newRoad, newPhone));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText(EditAddressActivity.this, "Edit thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditAddressActivity.this, "Edit failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void deleteAddress(int id) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Void> call = userApiService.removeAddress(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText(EditAddressActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditAddressActivity.this, "Co loi trong qua trinh xoa " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable throwable) {
                loadingDialog.hide();
                Toast.makeText(EditAddressActivity.this, "Khong the gui yeu cau xoa", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
