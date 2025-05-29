package com.ndroid.ecommerce.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ndroid.ecommerce.R;

public class CheckoutActivity extends AppCompatActivity {
    private TextView tvChooseAddress;
    private EditText etName, etCity, etRoad, etPhoneNumber;
    private EditText etCardNumber, etExpiryDate, etCVV, etCardName;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCreditCard, rbPayPal, rbCashOnDelivery;
    private LinearLayout creditCardLayout;
    private Button btnPlaceOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvChooseAddress = findViewById(R.id.tvChooseAddress);
        // Shipping information
        etName = findViewById(R.id.etName);
        etCity = findViewById(R.id.etCity);
        etRoad = findViewById(R.id.etRoad);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);

        // Payment information
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCreditCard = findViewById(R.id.rbCreditCard);
        rbPayPal = findViewById(R.id.rbPayPal);
        rbCashOnDelivery = findViewById(R.id.rbCashOnDelivery);
        creditCardLayout = findViewById(R.id.creditCardLayout);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiryDate = findViewById(R.id.etExpiryDate);
        etCVV = findViewById(R.id.etCVV);
        etCardName = findViewById(R.id.etCardName);

        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
    }

    private void setupListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbCreditCard) {
                creditCardLayout.setVisibility(View.VISIBLE);
            } else {
                creditCardLayout.setVisibility(View.GONE);
            }
        });

        btnPlaceOrder.setOnClickListener(v -> {
            if (validateInputs()) {
                processOrder();
            }
        });

        tvChooseAddress.setOnClickListener(l -> {
            startActivityForResult(new Intent(CheckoutActivity.this, ChooseAddressActivity.class), 100);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String name = data.getStringExtra("name");
            String phone = data.getStringExtra("phone");
            String road = data.getStringExtra("road");
            String city = data.getStringExtra("city");

            etName.setText(name);
            etPhoneNumber.setText(phone);
            etRoad.setText(road);
            etCity.setText(city);
        }
    }

    private boolean validateInputs() {
        // Validate shipping information
        if (TextUtils.isEmpty(etName.getText())) {
            showError("Please enter your address");
            return false;
        }
        if (TextUtils.isEmpty(etCity.getText())) {
            showError("Please enter your city");
            return false;
        }
        if (TextUtils.isEmpty(etRoad.getText())) {
            showError("Please enter your state");
            return false;
        }
        if (TextUtils.isEmpty(etPhoneNumber.getText())) {
            showError("Please enter your ZIP code");
            return false;
        }

        // Validate payment information based on selected method
        int selectedPaymentMethod = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentMethod == R.id.rbCreditCard) {
            if (TextUtils.isEmpty(etCardNumber.getText()) || etCardNumber.getText().length() != 16) {
                showError("Please enter a valid 16-digit card number");
                return false;
            }
            if (TextUtils.isEmpty(etExpiryDate.getText()) || !isValidExpiryDate(etExpiryDate.getText().toString())) {
                showError("Please enter a valid expiry date (MM/YY)");
                return false;
            }
            if (TextUtils.isEmpty(etCVV.getText()) || etCVV.getText().length() != 3) {
                showError("Please enter a valid 3-digit CVV");
                return false;
            }
            if (TextUtils.isEmpty(etCardName.getText())) {
                showError("Please enter the name on card");
                return false;
            }
        }

        return true;
    }

    private boolean isValidExpiryDate(String expiryDate) {
        if (expiryDate.length() != 5) return false;
        if (expiryDate.charAt(2) != '/') return false;

        try {
            int month = Integer.parseInt(expiryDate.substring(0, 2));
            int year = Integer.parseInt(expiryDate.substring(3, 5));
            return month >= 1 && month <= 12 && year >= 23; // Assuming current year is 2023
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void processOrder() {
        // Combine address fields
        String fullAddress = etName.getText().toString().trim() + ", " +
                etCity.getText().toString().trim() + ", " +
                etRoad.getText().toString().trim() + " " +
                etPhoneNumber.getText().toString().trim();

        // Get payment method
        String paymentMethod;
        int selectedPaymentMethod = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentMethod == R.id.rbCreditCard) {
            paymentMethod = "Credit Card ending in " + etCardNumber.getText().toString().substring(12);
        } else if (selectedPaymentMethod == R.id.rbPayPal) {
            paymentMethod = "PayPal";
        } else {
            paymentMethod = "Cash on Delivery";
        }

        // Pass the collected data back to ShoppingCartActivity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("shipping_address", fullAddress);
        resultIntent.putExtra("payment_method", paymentMethod);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
} 