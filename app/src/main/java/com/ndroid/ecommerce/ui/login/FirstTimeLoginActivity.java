package com.ndroid.ecommerce.ui.login;

import static android.widget.Toast.LENGTH_SHORT;
import static com.ndroid.ecommerce.util.StringUtil.isValidEmail;
import static com.ndroid.ecommerce.util.StringUtil.isValidName;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.databinding.ActivityFirstTimeLoginBinding;
import com.ndroid.ecommerce.util.LoadingDialog;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.ConfirmEmailRequest;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstTimeLoginActivity extends AppCompatActivity {

    private ActivityFirstTimeLoginBinding binding;
    private UserInfo userInfo;
    private String username;
    private final UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFirstTimeLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        userInfo = (UserInfo) intent.getSerializableExtra("user_info");
        username = intent.getStringExtra("username");
        Toast.makeText(this, String.valueOf(userInfo.getId()), LENGTH_SHORT).show();

        binding.btnStart.setOnClickListener(l -> {
            String displayName = binding.etUsername.getText().toString().trim();
            if (!isValidName(displayName)) {
                binding.etUsername.setError("Tên không hợp lệ!");
                return;
            }
            String email = binding.etEmail.getText().toString().trim();
            if (!isValidEmail(email)) {
                binding.etEmail.setError("Email không hợp lệ!");
                return;
            }

            confirmEmail(email, username);
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
                    Intent intent1 = new Intent(FirstTimeLoginActivity.this, EmailVerificationActivity.class);
                    intent1.putExtra("user_info", userInfo);
                    intent1.putExtra("display_name", binding.etUsername.getText().toString().trim());
                    intent1.putExtra("email", email);
                    intent1.putExtra("username", username);
                    startActivity(intent1);
                } else Toast.makeText( FirstTimeLoginActivity.this,"Co loi: "+response.code(), LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable throwable) {
                Log.w("CONFIRM_EMAIL", throwable.getMessage());
            }
        });
    }

}