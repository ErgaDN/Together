package com.example.together;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.textfield.TextInputEditText;

public class ForgotPassword extends AppCompatActivity {

    private ImageButton backBtn;
    private TextInputEditText editTextEmail;
    private Button recoverBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backBtn = findViewById(R.id.backBtn);
        editTextEmail = findViewById(R.id.email);
        recoverBtn = findViewById(R.id.recoverBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher();
            }
        });
    }
}