package com.example.together;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class clientEditProfile extends AppCompatActivity {

    EditText editFirstName, editLastName, editphone, editEmail, editPassword;
    Button saveButton;
    String firstNameUser, lastNameUser, phoneUser, emailUser, passwordUser,usernameUser;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_edit_profile);

//        reference = FirebaseDatabase.getInstance().getReference("clients");

        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editphone = findViewById(R.id.editPhoneNumber);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        saveButton = findViewById(R.id.btn_save);

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFifstNameChanged() || isLastNameChanged() || isPhoneChanged()) {
                    Toast.makeText(clientEditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(clientEditProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isFifstNameChanged(){
        if (!firstNameUser.equals(editFirstName.getText().toString())){
            reference.child(usernameUser).child("Firs Name").setValue(editFirstName.getText().toString());
            firstNameUser = editFirstName.getText().toString();
            return true;
        } else{
            return false;
        }
    }
    public boolean isLastNameChanged(){
        if (!lastNameUser.equals(editLastName.getText().toString())){
            reference.child(usernameUser).child("Last Name").setValue(editLastName.getText().toString());
            lastNameUser = editLastName.getText().toString();
            return true;
        } else{
            return false;
        }
    }
    public boolean isPhoneChanged(){
        if (!phoneUser.equals(editphone.getText().toString())){
            reference.child(usernameUser).child("Phone Number").setValue(editphone.getText().toString());
            lastNameUser = editphone.getText().toString();
            return true;
        } else{
            return false;
        }
    }
//    public boolean isEmailChanged(){
//        if (!emailUser.equals(editEmail.getText().toString())){
//            reference.child(usernameUser).child("Email").setValue(editEmail.getText().toString());
//            emailUser = editEmail.getText().toString();
//            return true;
//        } else{
//            return false;
//        }
//    }

//    public boolean isPasswordChanged(){
//        if (!passwordUser.equals(editPassword.getText().toString())){
//            reference.child(usernameUser).child("password").setValue(editPassword.getText().toString());
//            passwordUser = editPassword.getText().toString();
//            return true;
//        } else{
//            return false;
//        }
//    }

    public void showData(){
        Intent intent = getIntent();

        firstNameUser = intent.getStringExtra("First Name");
        lastNameUser = intent.getStringExtra("Last name");
        phoneUser = intent.getStringExtra("Phone number");
//        emailUser = intent.getStringExtra("Email");
//        passwordUser = intent.getStringExtra("Password");

        editFirstName.setText(firstNameUser);
        editLastName.setText(lastNameUser);
        editphone.setText(phoneUser);
//        editEmail.setText(emailUser);
//        editPassword.setText(passwordUser);
    }
}