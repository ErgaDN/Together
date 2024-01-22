package com.example.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class Agricultural extends AppCompatActivity {
    Button btn_editing_products, btn_distribution_center, btn_order_summary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agricultural);
        btn_editing_products = findViewById(R.id.btn_editing_products);
        btn_distribution_center = findViewById(R.id.btn_distribution_center);
        btn_order_summary = findViewById(R.id.btn_order_summary);

        btn_editing_products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(getApplicationContext(), EditingProducts.class);
                startActivity(intent);
                finish();
            }
        });

        btn_distribution_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(getApplicationContext(), DistributionCenter.class);
                startActivity(intent);
                finish();
            }
        });

        btn_order_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(getApplicationContext(), OrderSummary.class);
                startActivity(intent);
                finish();
            }
        });

    }
}