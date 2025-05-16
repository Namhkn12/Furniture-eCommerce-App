package com.ndroid.ecommerce.ui.login;

import static android.widget.Toast.LENGTH_SHORT;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityEmailVerificationBinding;
import com.ndroid.ecommerce.ui.UserDashboardActivity;
import com.ndroid.ecommerce.util.LoadingDialog;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.ConfirmEmailRequest;
import api.user.request.UpdateEmailRequest;
import api.user.request.UpdateNameRequest;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailVerificationActivity extends AppCompatActivity {

    private ActivityEmailVerificationBinding binding;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private UserInfo userInfo;
    private String username;
    private String displayName;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEmailVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent bundle = getIntent();
        userInfo = (UserInfo) bundle.getSerializableExtra("user_info");
        username = bundle.getStringExtra("username");
        displayName = bundle.getStringExtra("display_name");
        email = bundle.getStringExtra("email");

        binding.btnStart.setOnClickListener(l -> verifyAndStartApp());

        binding.btnResend.setOnClickListener(l -> confirmEmail(email, username));

        binding.btnReturn.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
    }

    private void verifyAndStartApp() {
        String code = binding.etCode.getText().toString().trim();
        if (code.length() > 6) {
            binding.etCode.setError("Mã không dài quá 6 kí tự");
            return;
        } else if (code.isBlank()) {
            binding.etCode.setError("Không được để trống");
            return;
        }

        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.updateEmail(new UpdateEmailRequest(username, email, code));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    changeDisplayName(displayName); //This call also close the activity and start main
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                loadingDialog.hide();
                Log.w("USERNAME_CHANGE_ERROR", throwable.getMessage());
            }
        });
    }

    private void confirmEmail(String email, String username) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<Boolean> call = userApiService.requestConfirmEmail(new ConfirmEmailRequest(email, username));
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Toast.makeText( EmailVerificationActivity.this,"Đã gửi mã", LENGTH_SHORT).show();
                    startResendTimer(binding.btnResend);
                } else Toast.makeText( EmailVerificationActivity.this,"Co loi: "+response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
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


    private void changeDisplayName(String newDisplayName) {
        LoadingDialog loadingDialog = new LoadingDialog(this);
        loadingDialog.show();
        Call<String> call = userApiService.changeName(userInfo.getId(), new UpdateNameRequest(newDisplayName));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                loadingDialog.hide();
                if (response.isSuccessful()) {
                    Intent intent = new Intent(EmailVerificationActivity.this, UserDashboardActivity.class);
                    intent.putExtra("user_info", userInfo);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else Toast.makeText( EmailVerificationActivity.this,"Co loi: "+response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                Log.w("USERNAME_CHANGE_ERROR", throwable.getMessage());
            }
        });
    }
}