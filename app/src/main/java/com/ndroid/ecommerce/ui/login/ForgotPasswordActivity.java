package com.ndroid.ecommerce.ui.login;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ndroid.ecommerce.util.StringUtil.isValidPassword;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityForgotPasswordBinding;
import com.ndroid.ecommerce.util.LoadingDialog;
import com.ndroid.ecommerce.util.StringUtil;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.ChangePasswordRequest;
import api.user.request.UpdatePasswordRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ActivityForgotPasswordBinding binding;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSendCode.setOnClickListener(l -> sendCode());
        binding.btnResend.setOnClickListener(l -> resendCode());
        binding.btnChangePassword.setOnClickListener(l -> verifyAndChangePassword());
        binding.btnReturn.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void sendCode() {
        String username = binding.etUsername.getText().toString().trim();
        if (!StringUtil.isValidUsername(username)) {
            binding.etUsername.setError("Tên tài khoản không đúng định dạng");
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestPasswordChange(new ChangePasswordRequest(username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    binding.firstSection.setVisibility(GONE);
                    binding.secondSection.setVisibility(VISIBLE);
                } else binding.etUsername.setError("Tài khoản không tồn tại");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void verifyAndChangePassword() {
        String username = binding.etUsername.getText().toString().trim();
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
        if (!isValidPassword(password)) {
            binding.etPassword.setError("Mật khẩu không hợp lệ");
            return;
        }
        if (!isValidPassword(confirmPassword)) {
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
                    Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thành công", LENGTH_SHORT).show();
                    finish();
                } else binding.etCode.setError("Mã xác nhận không đúng");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
                Log.w("PASSWORD_CHANGE", throwable.getMessage());
            }
        });
    }

    private void resendCode() {
        String username = binding.etUsername.getText().toString().trim();
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestPasswordChange(new ChangePasswordRequest(username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText( ForgotPasswordActivity.this,"Đã gửi mã", LENGTH_SHORT).show();
                    startResendTimer(binding.btnResend);
                } else binding.etUsername.setError("Tên tài khoản không tồn tại");
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