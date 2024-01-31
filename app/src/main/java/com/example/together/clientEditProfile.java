package com.example.together;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class clientEditProfile extends AppCompatActivity {

    EditText editFirstName, editLastName, editPhone;
    Button saveButton;
    String firstNameUser, lastNameUser, phoneUser;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_edit_profile);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhoneNumber);
        saveButton = findViewById(R.id.btn_save);

        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        //showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFifstNameChanged, isLastNameChanged, isPhoneChanged;
                isFifstNameChanged = isFifstNameChanged();
                isLastNameChanged = isLastNameChanged();
                isPhoneChanged = isPhoneChanged();

                if (isFifstNameChanged || isLastNameChanged || isPhoneChanged ) {
                    Toast.makeText(clientEditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(clientEditProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), ClientProfile.class);
                startActivity(intent);
            }
        });
    }

    public boolean isFifstNameChanged(){
        if (editFirstName != null && editFirstName.length() > 0){//
            firstNameUser= editFirstName.getText().toString();
            updateFirstName(firstNameUser);
            return true;
        } else{
            return false;
        }
    }

    public boolean isLastNameChanged(){
        if (editLastName != null && editLastName.length() > 0){//
            lastNameUser= editLastName.getText().toString();
            updateLastName(lastNameUser);
            return true;
        } else{
            return false;
        }
    }

    public boolean isPhoneChanged(){
        if (editPhone != null && editPhone.length() > 0){//
            phoneUser= editPhone.getText().toString();
            updatePhone(phoneUser);
            return true;
        } else{
            return false;
        }
    }

    private void updateFirstName(String firstName) {

        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = mStore.collection("clients").document(userID);

        Map<String, Object> update = new HashMap<>();
        update.put("First Name", firstName);
        documentReference.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "first name update" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "first name fail" + userID);
            }
        });
    }

    private void updateLastName(String lastName) {

        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = mStore.collection("clients").document(userID);

        Map<String, Object> update = new HashMap<>();
        update.put("Last Name", lastName);
        documentReference.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "last name update" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "last name fail" + userID);
            }
        });
    }

    private void updatePhone(String phone) {

        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = mStore.collection("clients").document(userID);

        Map<String, Object> update = new HashMap<>();
        update.put("Phone Number", phone);
        documentReference.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "phone number update" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "phone number fail" + userID);
            }
        });
    }


    public void showData(){
        Intent intent = getIntent();

        firstNameUser = intent.getStringExtra("First Name");
        lastNameUser = intent.getStringExtra("Last name");
        phoneUser = intent.getStringExtra("Phone number");


        editFirstName.setText(firstNameUser);
        editLastName.setText(lastNameUser);
        editPhone.setText(phoneUser);

    }
}