package com.ndroid.ecommerce.ui.setting;

import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.R;
import com.ndroid.ecommerce.databinding.ActivityAccountSecuritySettingBinding;
import com.ndroid.ecommerce.util.LoadingDialog;

import java.time.LocalDate;

import api.user.UserApiClient;
import api.user.UserApiService;
import api.user.request.UpdateDOBRequest;
import api.user.request.UpdateGenderRequest;
import api.user.request.UpdateNameRequest;
import api.user.request.UpdatePhoneRequest;
import model.user.UserInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountSecuritySettingActivity extends AppCompatActivity {

    private ActivityAccountSecuritySettingBinding binding;
    private UserApiService userApiService = UserApiClient.getRetrofit().create(UserApiService.class);
    private UserInfo userInfo;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountSecuritySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userInfo = (UserInfo) getIntent().getSerializableExtra("user_info");
        username = getIntent().getStringExtra("username");

        binding.backButton.setOnClickListener(l -> getOnBackPressedDispatcher().onBackPressed());
        binding.userName.setOnClickListener(l -> showEditUsernameDialog());
        binding.gender.setOnClickListener(l -> showGenderDialog());
        binding.dateOfBirth.setOnClickListener(l -> showCustomDobDialog());
        binding.phoneNumber.setOnClickListener(l -> showPhoneDialog());
        binding.changePassword.setOnClickListener(l -> startChangePasswordActivity());
        binding.changeEmail.setOnClickListener(l -> startChangeEmailActivity());
    }

    private void startChangeEmailActivity() {
        Intent intent = new Intent(this, ChangeEmailActivity.class);
        intent.putExtra("username", username);
        intent.putExtra("email", userInfo.getEmail());
        startActivity(intent);
    }

    private void showEditUsernameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_username, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText input = dialogView.findViewById(R.id.editUsernameInput);
        input.setText(userInfo.getDisplayName());
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String newUsername = input.getText().toString().trim();
            if (!newUsername.isEmpty() && !newUsername.equals(userInfo.getDisplayName())) {
                LoadingDialog loadingDialog = new LoadingDialog(this);
                loadingDialog.show();
                Call<String> call = userApiService.changeName(userInfo.getId(), new UpdateNameRequest(newUsername));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            showToast("Doi ten thanh cong");
                            loadingDialog.hide();
                            userInfo.setDisplayName(newUsername);
                        } else showToast("Co loi: "+response.code());
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        loadingDialog.hide();
                        Log.w("USERNAME_CHANGE_ERROR", throwable.getMessage());
                    }
                });

                dialog.dismiss();
            } else {
                input.setError("Không được để trống/Ten khong duoc trung ten cu");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showGenderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_gender, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        RadioGroup genderGroup = dialogView.findViewById(R.id.genderRadioGroup);
        RadioButton radioMale = dialogView.findViewById(R.id.radioMale);
        RadioButton radioFemale = dialogView.findViewById(R.id.radioFemale);
        RadioButton radioOther = dialogView.findViewById(R.id.radioOther);
        TextView warningTv = dialogView.findViewById(R.id.warningChooseGenderTv);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String gender = "";
            int selectedId = genderGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.radioMale) {
                gender = "Nam";
            } else if (selectedId == R.id.radioFemale) {
                gender = "Nữ";
            } else if (selectedId == R.id.radioOther) {
                gender = "Khác";
            }

            if (!gender.isEmpty()) {
                LoadingDialog loadingDialog = new LoadingDialog(this);
                loadingDialog.show();
                Call<String> call = userApiService.changeGender(userInfo.getId(), new UpdateGenderRequest(gender));
                String finalGender = gender;
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            makeText(AccountSecuritySettingActivity.this, "Done", LENGTH_SHORT).show();
                            loadingDialog.hide();
                            userInfo.setGender(finalGender);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        loadingDialog.hide();
                    }
                });
                dialog.dismiss();
            } else warningTv.setVisibility(VISIBLE);
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showCustomDobDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_dob, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        LocalDate dob = userInfo.getDateOfBirth();
        DatePicker datePicker = dialogView.findViewById(R.id.datePicker);
        if (dob != null) {
            datePicker.updateDate(dob.getYear(), dob.getMonthValue() - 1, dob.getDayOfMonth());
        } else {
            datePicker.updateDate(2025, 0, 1);
        }

        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String day = String.valueOf(datePicker.getDayOfMonth());
            String month = String.valueOf(datePicker.getMonth() + 1);// Month is 0-based
            String monthRes;
            if (Integer.parseInt(month) < 10) {
                monthRes = "0" + month;
            } else monthRes = month;
            String year = String.valueOf(datePicker.getYear());
            LocalDate date = LocalDate.parse(year + "-" + monthRes + "-" + day);

            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.show();
            Call<String> call = userApiService.changeDob(userInfo.getId(), new UpdateDOBRequest(date));
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    loadingDialog.hide();
                    if (response.isSuccessful()) {
                        makeText(AccountSecuritySettingActivity.this, "Đổi thành công", LENGTH_SHORT).show();
                        call.request().body();
                        userInfo.setDateOfBirth(date);
                    } else {
                        makeText(AccountSecuritySettingActivity.this, "ERROR " + response.code(), LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable throwable) {
                    loadingDialog.hide();
                    Log.w("CHANGE_DOB", throwable.getMessage());
                }
            });
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showPhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_phone_number, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        EditText input = dialogView.findViewById(R.id.editPhoneInput);
        input.setText(userInfo.getPhoneNumber());
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String phone = input.getText().toString().trim();
            if (!phone.isEmpty() && phone.matches("^\\d{9,15}$")) { // basic validation
                LoadingDialog loadingDialog = new LoadingDialog(this);
                loadingDialog.show();
                Call<String> call = userApiService.changePhoneNumber(userInfo.getId(), new UpdatePhoneRequest(phone));
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            makeText(AccountSecuritySettingActivity.this, "Đổi thành công", LENGTH_SHORT).show();
                            loadingDialog.hide();
                            userInfo.setPhoneNumber(phone);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        loadingDialog.hide();
                        showToast("Error reading/Connection error");
                        Log.w("CHANGE_PHONE", throwable.getMessage());
                    }
                });
                dialog.dismiss();
            } else {
                input.setError("Số điện thoại không hợp lệ");
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void startChangePasswordActivity() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, LENGTH_SHORT).show();
    }
}