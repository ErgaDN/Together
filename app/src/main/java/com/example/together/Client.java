package com.example.together;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class Client extends AppCompatActivity {
    ImageButton btn_profile, btn_client_cart, btn_logout, filterProductBtn;
    Toolbar toolbar;
    private EditText searchProductsEt;
    private TextView filteredProductsTv;
    private RecyclerView productsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        btn_profile = findViewById(R.id.btn_profile);
        btn_client_cart = findViewById(R.id.btn_client_cart);
        btn_logout = findViewById(R.id.btn_logout);
        searchProductsEt = findViewById(R.id.searchProductsEt);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        productsRv = findViewById(R.id.productsRv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadProducts();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Client.this, ClientProfile.class);
                startActivity(intent);
                finish();
            }
        });

        btn_client_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Client.this, ClientCart.class);
                startActivity(intent);
                finish();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Client.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        

    }

    private void loadProducts() {
    }
}