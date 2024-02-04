package com.example.together;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class OrderDeatailsClient extends AppCompatActivity {

    private String orderId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_deatails_client);

        Intent intent = getIntent();
        orderId = intent.getStringExtra("orderId");
    }
}