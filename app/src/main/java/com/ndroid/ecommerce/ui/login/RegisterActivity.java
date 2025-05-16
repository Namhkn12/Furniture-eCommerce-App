package com.ndroid.ecommerce.ui.login;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityRegisterBinding;
import com.ndroid.ecommerce.util.LoadingDialog;
import com.ndroid.ecommerce.util.StringUtil;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private final UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            String confirm = binding.etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", LENGTH_SHORT).show();
                return;
            }

            if (!StringUtil.isValidUsername(username)) {
                binding.etUsername.setError("Tên tài khoản không hợp lệ");
                return;
            }
            if (!StringUtil.isValidPassword(password)) {
                binding.etPassword.setError("Mật khẩu không hợp lệ");
                return;
            }

            if (!password.equals(confirm)) {
                Toast.makeText(this, "Mật khẩu không khớp!", LENGTH_SHORT).show();
                return;
            }

            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();
            Call<Boolean> call = userApiService.register(new RegisterRequest(username, password));
            call.enqueue(new Callback<Boolean>() {
                @Override
                public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                    if (response.isSuccessful()) {
                        loadingDialog.hide();

                        Intent result = new Intent();
                        result.putExtra("username", username);
                        result.putExtra("password", password);
                        setResult(RESULT_OK, result);
                        finish();
                    } else {
                        loadingDialog.hide();
                        binding.etUsername.setError("Tên tài khoản đã tồn tại");
                    }
                }

                @Override
                public void onFailure(Call<Boolean> call, Throwable throwable) {

                }
            });
        });

        binding.tvBackToLogin.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }
}
