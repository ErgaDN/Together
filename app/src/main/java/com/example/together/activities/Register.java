package com.example.together.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.together.R;
import com.google.firebase.auth.FirebaseAuth;


public class Register extends AppCompatActivity {

    Button btn_cl, btn_cen, btn_ag;
    FirebaseAuth mAuth;
    TextView textView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        btn_cl = findViewById(R.id.btn_register_client);
        btn_ag = findViewById(R.id.btn_register_seller);
        textView = findViewById(R.id.loginNow);

        textView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        btn_cl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterClient.class);
                startActivity(intent);
                finish();
            }
        });

        btn_ag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegisterSeller.class);
                startActivity(intent);
                finish();
            }
        });




    }
}