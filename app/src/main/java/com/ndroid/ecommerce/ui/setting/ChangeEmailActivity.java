package com.ndroid.ecommerce.ui.setting;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.ndroid.ecommerce.util.StringUtil.isValidEmail;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityChangeEmailBinding;
import com.ndroid.ecommerce.util.LoadingDialog;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.CodeRequest;
import api.user.request.ConfirmEmailRequest;
import api.user.request.UpdateEmailRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailActivity extends AppCompatActivity {

    private ActivityChangeEmailBinding binding;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private String username;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        username = getIntent().getStringExtra("username");
        email = getIntent().getStringExtra("email");

        binding.currentMailTv.setText("Email hiện tại: " + email);

        binding.backButton.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());

        binding.btnSendCode.setOnClickListener(l -> sendChangeMailCode(username, email));

        binding.btnChangeEmail.setOnClickListener(l -> verifyMailCode(username));
        binding.btnResend.setOnClickListener(l -> resendCode(email, username, binding.btnResend));

        binding.btnConfirmChangeEmail.setOnClickListener(l -> verifyAndChangeEmail(username));
        binding.btnResendConfirm.setOnClickListener(l -> resendCode(binding.etEmail.getText().toString().trim(), username, binding.btnResendConfirm));
    }

    private void sendChangeMailCode(String username, String email) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestConfirmEmail(new ConfirmEmailRequest(email, username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    binding.changeSection.setVisibility(VISIBLE);
                    binding.sendCodeSection.setVisibility(GONE);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void verifyMailCode(String username) {
        String code = binding.etCode.getText().toString().trim();
        if (code.length() > 6) {
            binding.etCode.setError("Tối đa 6 kí tự");
            return;
        } else if (code.isBlank()) {
            binding.etCode.setError("Không được để trống");
            return;
        }

        String email = binding.etEmail.getText().toString().trim();
        if (!isValidEmail(email)) {
            binding.etEmail.setError("Địa chỉ mail không hợp lệ");
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.verifyEmailCode(new CodeRequest(username, code));
        Log.w("USERNAME", username);
        Log.w("CODE", code);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    requestNewMailVerify();
                } else binding.etCode.setError("Mã xác nhận không đúng");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void requestNewMailVerify() {
        String newEmail = binding.etEmail.getText().toString().trim();

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestConfirmEmail(new ConfirmEmailRequest(newEmail, username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    binding.confirmSection.setVisibility(VISIBLE);
                    binding.changeSection.setVisibility(GONE);
                } else Toast.makeText(ChangeEmailActivity.this, response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void verifyAndChangeEmail(String username) {
        String code = binding.etConfirmCode.getText().toString().trim();
        if (code.length() > 6) {
            binding.etConfirmCode.setError("Tối đa 6 kí tự");
            return;
        } else if (code.isBlank()) {
            binding.etConfirmCode.setError("Không được để trống");
            return;
        }
        String newEmail = binding.etEmail.getText().toString().trim();

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.updateEmail(new UpdateEmailRequest(username, newEmail, code));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText(ChangeEmailActivity.this, "Đổi email thành công", Toast.LENGTH_SHORT).show();
                    finish();
                } else binding.etConfirmCode.setError("Mã xác nhận không đúng");
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
            }
        });
    }

    private void resendCode(String email, String username, Button button) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestConfirmEmail(new ConfirmEmailRequest(email, username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText( ChangeEmailActivity.this,"Đã gửi mã", LENGTH_SHORT).show();
                    startResendTimer(binding.btnResend);
                } else Toast.makeText( ChangeEmailActivity.this,"Co loi: "+response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
                Log.w("CONFIRM_EMAIL", throwable.getMessage());
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