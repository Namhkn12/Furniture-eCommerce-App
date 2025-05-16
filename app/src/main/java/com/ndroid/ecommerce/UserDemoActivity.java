package com.ndroid.ecommerce;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import api.user.UserApiClient;
import api.user.UserApiService;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDemoActivity extends AppCompatActivity {
    private TextView textUserId, textUsername, textUserAddress, textUserPhoneNum;
    private ImageView imageUser;
    private Button buttonSearch;
    private EditText editSearchId;

    //Retrofit
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private Call<UserInfo> call = userApiService.getUser(1); //get user with id = 1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_demo);

        initGui();

        buttonSearch.setOnClickListener(l -> {
            String searchId = editSearchId.getText().toString();
            if (searchId.isBlank()) {
                Toast.makeText(this, "Please type in a number in EditText", Toast.LENGTH_SHORT).show();
                return;
            }

            call.enqueue(new Callback<UserInfo>() {
                @Override
                public void onResponse(Call<UserInfo> call, Response<UserInfo> response) {
                    if (response.isSuccessful()) {
                        UserInfo user = response.body();
                        Log.d("GET_USER", "User with id: " + user.getId() + " has displayName: " + user.getDisplayName());

                        textUserId.setText(String.valueOf(user.getId()));
                        textUsername.setText(String.valueOf(user.getDisplayName()));
//                        textUserAddress.setText(String.valueOf(user.getAddress()));
                        textUserPhoneNum.setText(String.valueOf(user.getPhoneNumber()));
                    } else {
                        Log.e("GET_USER", "Server error: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<UserInfo> call, Throwable throwable) {
                    Log.e("GET_USER", "Network error: " + throwable.getMessage());
                }
            });
            //Call to image
            Glide.with(this).load("http://192.168.1.101:8080/api/image/users/1").into(imageUser);
        });

    }

    private void initGui() {
        textUserId = findViewById(R.id.textUserId);
        textUsername = findViewById(R.id.textUserName);
        textUserAddress = findViewById(R.id.textUserAddress);
        textUserPhoneNum = findViewById(R.id.textUserPhoneNum);
        imageUser = findViewById(R.id.imgUser);
        editSearchId = findViewById(R.id.editIdSearch);
        buttonSearch = findViewById(R.id.buttonSearch);
    }
}