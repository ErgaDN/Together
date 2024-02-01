package com.example.together;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ClientCart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_cart);

        getSupportActionBar().setTitle("הסל שלי");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}