package com.example.together;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

public class EditingProducts extends AppCompatActivity {
    Button btn_adding_products;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editing_products);
        btn_adding_products = findViewById(R.id.btn_adding_products);

        //TODO: add onclick to btn_adding_products

    }
}