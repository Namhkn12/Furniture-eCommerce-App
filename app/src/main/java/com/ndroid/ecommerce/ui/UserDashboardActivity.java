package com.ndroid.ecommerce.ui;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.databinding.ActivityUserDashboardBinding;
import com.ndroid.ecommerce.ui.login.LoginActivity;
import com.ndroid.ecommerce.ui.setting.AccountAddressSettingActivity;
import com.ndroid.ecommerce.ui.setting.AccountSecuritySettingActivity;
import com.ndroid.ecommerce.util.LoadingDialog;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import api.user.UserApiClient;
import api.user.UserApiService;
import model.user.UserInfo;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDashboardActivity extends AppCompatActivity {

    private ActivityUserDashboardBinding binding;
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    uploadImage();
                }
            });
    private final UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private UserInfo userInfo;
    private String username;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        Serializable serializable = intent.getSerializableExtra("user_info");

        if (serializable != null) {
            // User logged in, hide login button
            binding.userDashboardLoginButton.setVisibility(INVISIBLE);
            binding.userDashboardLogoutButton.setVisibility(VISIBLE);

            initUserInfo(serializable);
            username = intent.getStringExtra("username");
        } else {
            binding.userDashboardLoginButton.setVisibility(VISIBLE);
            binding.userDashboardLogoutButton.setVisibility(INVISIBLE);
        }

        binding.userDashboardLogoutButton.setOnClickListener(l -> showLogoutDialog());
        binding.userDashboardAccountAndSecuritySettingText.setOnClickListener(l -> openAccountSettingActivity());
        binding.userDashboardAddressSettingText.setOnClickListener(l -> openAddressEditActivity());
        binding.userDashboardImage.setOnClickListener(l -> chooseImage());
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadUserInfo();
    }

    private void initUserInfo(Serializable serializable) {
        userInfo = (UserInfo) serializable;
        binding.userNameTv.setText(userInfo.getDisplayName());
        Glide.with(this).load(UserApiClient.USER_ENDPOINT_URL + "image/users/" + userInfo.getId()).into(binding.userDashboardImage);
    }

    private void reloadUserInfo() {
        Glide.with(UserDashboardActivity.this).load(UserApiClient.USER_ENDPOINT_URL + "image/users/" + userInfo.getId()).into(binding.userDashboardImage);
        Call<UserInfo> call = userApiService.getUser(userInfo.getId());
        call.enqueue(new Callback<UserInfo>() {
            @Override
            public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                if (response.isSuccessful()) {
                    userInfo = response.body();
                    binding.userNameTv.setText(userInfo.getDisplayName());
                }
            }

            @Override
            public void onFailure(Call<UserInfo> call, Throwable throwable) {

            }
        });
    }

    private void chooseImage() {
        imagePickerLauncher.launch("image/*");
    }

    private void uploadImage() {
        try {
            ContentResolver contentResolver = getContentResolver();
            String fileName = getFileName(selectedImageUri);
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            inputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(selectedImageUri)), imageBytes);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", fileName, requestFile);

            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();
            userApiService.uploadImage(userInfo.getId(), body).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    loadingDialog.hide();
                    if (response.isSuccessful()) {
                        binding.userDashboardImage.setImageURI(selectedImageUri);
                        Toast.makeText(UserDashboardActivity.this, "Tải ảnh thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserDashboardActivity.this, "Failed" + response.code() + " " + response.body(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    loadingDialog.hide();
                    Toast.makeText(UserDashboardActivity.this, "Upload Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Log.w("UPLOAD_IMAGE", e);
            Toast.makeText(this, "Error reading image", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private void openAccountSettingActivity() {
        if (userInfo == null) {
            Toast.makeText(this, "You must be logged in", LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(UserDashboardActivity.this,  AccountSecuritySettingActivity.class);
        intent.putExtra("user_info", userInfo);
        intent.putExtra("username", username);
        this.startActivity(intent);
    }

    private void openAddressEditActivity() {
        if (userInfo == null) {
            Toast.makeText(this, "You must be logged in", LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(UserDashboardActivity.this,  AccountAddressSettingActivity.class);
        intent.putExtra("user_info", userInfo);
        this.startActivity(intent);
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_logout_confirm, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // prevent outside touch cancel

        Button btnCancel = view.findViewById(R.id.btnCancel);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnLogout.setOnClickListener(v -> {
            // Clear session
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Go to LoginActivity
            Intent intent = new Intent(UserDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            dialog.dismiss();
        });

        dialog.show();
    }
}