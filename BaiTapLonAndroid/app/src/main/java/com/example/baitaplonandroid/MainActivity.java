package com.example.baitaplonandroid;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Launch the ReviewActivity immediately
        Intent intent = new Intent(this, ReviewActivity.class);
        startActivity(intent);

        // Optional: finish MainActivity so user can't go back to it
        // finish();
    }
}