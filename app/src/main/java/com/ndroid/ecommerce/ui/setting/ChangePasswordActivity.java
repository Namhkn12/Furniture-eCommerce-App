package com.ndroid.ecommerce.ui.setting;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityChangePasswordBinding;
import com.ndroid.ecommerce.util.LoadingDialog;
import com.ndroid.ecommerce.util.StringUtil;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.ChangePasswordRequest;
import api.user.request.UpdatePasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private ActivityChangePasswordBinding binding;
    private String username;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = getIntent().getStringExtra("username");

        binding.backButton.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
        binding.btnSendCode.setOnClickListener(l -> sendCode(username));
        binding.btnResend.setOnClickListener(l -> resendCode(username));
        binding.btnChangePassword.setOnClickListener(l -> verifyAndChangePassword());

    }

    private void sendCode(String username) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestPasswordChange(new ChangePasswordRequest(username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    binding.resetSection.setVisibility(VISIBLE);
                    binding.btnSendCode.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void verifyAndChangePassword() {
        String code = binding.etCode.getText().toString().trim();
        if (code.length() > 6) {
            binding.etCode.setError("Mã không dài quá 6 kí tự");
            return;
        } else if (code.isBlank()) {
            binding.etCode.setError("Không được để trống");
            return;
        }

        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();
        if (!StringUtil.isValidPassword(password)) {
            binding.etPassword.setError("Mật khẩu không hợp lệ");
            return;
        }
        if (!StringUtil.isValidPassword(confirmPassword)) {
            binding.etConfirmPassword.setError("Mật khẩu không hợp lệ");
            return;
        }
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Mật khẩu chưa trùng nhau");
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.updatePassword(new UpdatePasswordRequest(username, code, password));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Đổi mật khẩu thành công", LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
                Log.w("PASSWORD_CHANGE", throwable.getMessage());
            }
        });
    }

    private void resendCode(String username) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestPasswordChange(new ChangePasswordRequest(username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText( ChangePasswordActivity.this,"Đã gửi mã", LENGTH_SHORT).show();
                    startResendTimer(binding.btnResend);
                } else Toast.makeText( ChangePasswordActivity.this,"Co loi: "+response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
                Log.w("RESEND_CODE", throwable.getMessage());
            }
        });
    }

    private void startResendTimer(Button button) {
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                button.setEnabled(false);
                button.setText("Gửi lại sau " + millisUntilFinished / 1000 + "s");
            }

            public void onFinish() {
                button.setEnabled(true);
                button.setText("Gửi lại mã");
            }
        }.start();
    }

}