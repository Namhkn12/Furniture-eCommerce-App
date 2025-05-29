package com.ndroid.ecommerce.ui.login;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.MyApplication;
import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.databinding.ActivityLoginBinding;
import com.ndroid.ecommerce.ui.product.HomeActivity;
import com.ndroid.ecommerce.util.LoadingDialog;
import com.ndroid.ecommerce.util.StringUtil;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.LoginRequest;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private final UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String username = prefs.getString("saved_username", null);
        String password = prefs.getString("saved_password", null);

        LoadingDialog loadingDialog = new LoadingDialog(this);

        if (username != null && password != null) {
            loadingDialog.show();
            Call<UserInfo> call = userApiService.login(new LoginRequest(username, password));
            call.enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                    if (response.isSuccessful()) {
                        loadingDialog.hide();
                        startAppActivity(response.body(), username);
                        makeToast("Login thành công");
                    }
                    else {
                        loadingDialog.hide();
                        makeToast("Tự động Login thất bại, hãy đăng nhập lại");
                        prefs.edit().clear().apply();
                        recreate();
                    }
                }

                @Override
                public void onFailure(Call<UserInfo> call, Throwable throwable) {
                    loadingDialog.hide();
                    makeToast("Khong the ket noi toi server (Internet?)");
                }
            });
            return;
        }

        binding.btnForgetPassword.setOnClickListener(l -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        // Login button action
        binding.btnLogin.setOnClickListener(v -> {
            String inputName = binding.etUsername.getText().toString().trim();
            String inputPass = binding.etPassword.getText().toString().trim();

            if (inputName.isEmpty() || inputPass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", LENGTH_SHORT).show();
                return;
            }

            if (!StringUtil.isValidUsername(inputName)) {
                binding.etUsername.setError("Tên tài khoản không hợp lệ");
                return;
            }
            if (!StringUtil.isValidPassword(inputPass)) {
                binding.etPassword.setError("Mật khẩu không hợp lệ");
                return;
            }

            loadingDialog.show();
            Call<UserInfo> call = userApiService.login(new LoginRequest(inputName, inputPass));
            call.enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                    if (response.isSuccessful()) {
                        loadingDialog.hide();
                        handleNewLogin(prefs, inputName, inputPass, response.body());
                    } else {
                        loadingDialog.hide();
                        makeToast("Đăng nhập thất bại");
                        binding.tvInccorectInfo.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<UserInfo> call, Throwable throwable) {
                    loadingDialog.hide();
                    makeToast("Error Connecting/Reading body");
                    Log.w("LOGIN_ERROR", throwable.getMessage());
                }
            });
        });

        // Navigate to Register screen
        binding.btRegister.setOnClickListener(v -> openRegisterActivity());
    }

    private void handleNewLogin(SharedPreferences prefs, String username, String password, UserInfo userInfo) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_remember_me, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        Button btnYes = dialogView.findViewById(R.id.btnYes);
        Button btnNo = dialogView.findViewById(R.id.btnNo);

        btnYes.setOnClickListener(view -> {
            prefs.edit().putString("saved_username", username).apply();
            prefs.edit().putString("saved_password", password).apply();
            dialog.dismiss();
            startAppActivity(userInfo, username);
        });

        btnNo.setOnClickListener(view -> {
            prefs.edit().remove("saved_username").apply();
            prefs.edit().remove("saved_password").apply();
            dialog.dismiss();
            startAppActivity(userInfo, username);
        });

        dialog.show();
    }

    private void openRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, 100);
    }

    //After user register
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String username = data.getStringExtra("username");
            String password = data.getStringExtra("password");
            binding.etUsername.setText(username);
            binding.etPassword.setText(password);
        }
    }

    private void startAppActivity(UserInfo userInfo, String username) {
        Toast.makeText(this, "Login thanh cong", LENGTH_SHORT).show();

        if (userInfo.getDisplayName().equals("New User") || userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
            Intent intent = new Intent(this, FirstTimeLoginActivity.class);
            intent.putExtra("user_info", userInfo);
            intent.putExtra("username", username);
            startActivity(intent);
            finish();
            return;
        }

        Intent intent = new Intent(this, HomeActivity.class);
//        intent.putExtra("user_info", userInfo);
//        intent.putExtra("username", username);
        MyApplication myApplication = (MyApplication) getApplication();
        myApplication.setUserInfo(userInfo);
        myApplication.setUsername(username);
        startActivity(intent);
        finish();
    }

    private void makeToast(String text) {
        Toast.makeText(this, text, LENGTH_SHORT).show();
    }
}
