package com.example.together.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.together.R;
import com.google.firebase.auth.FirebaseAuth;

public class OrderDetailsClient extends AppCompatActivity {

    private String orderId; // orderTo;

    //ui views
    private TextView orderIdTv, dateTv, orderStatusTv, totalItemsTv, amountTv, addressTv;
    private RecyclerView itemsRv;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_client);

        //init views
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);

        Intent intent = getIntent();
//        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadOrderDetails();
    }

    private void loadOrderDetails() {
        //load order details
//        DatabaseReference ref = FirebaseDatabase.getInstance()
    }
}