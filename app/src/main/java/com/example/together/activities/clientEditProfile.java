package com.example.together.activities;

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

import com.example.together.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class clientEditProfile extends AppCompatActivity {

    EditText editFirstName, editLastName, editPhone, editAddress;
    Button saveButton;
    String firstNameUser, lastNameUser, phoneUser, addressUser;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore mStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_edit_profile);
        getSupportActionBar().setTitle("עריכת פרופיל");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhone = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.select_address);
        saveButton = findViewById(R.id.btn_save);

        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        showUserData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isFifstNameChanged, isLastNameChanged, isPhoneChanged, isAddressChanged;
                isFifstNameChanged = isFirstNameChanged();
                isLastNameChanged = isLastNameChanged();
                isPhoneChanged = isPhoneChanged();
                isAddressChanged = isAddressChanged();


                if (isFifstNameChanged || isLastNameChanged || isPhoneChanged || isAddressChanged) {
                    Toast.makeText(clientEditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(clientEditProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), ClientProfile.class);
                startActivity(intent);
            }
        });
    }

    public boolean isFirstNameChanged(){
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
    public boolean isAddressChanged(){
        if (editAddress != null && editAddress.length() > 0){//
            addressUser= editAddress.getText().toString();
            updateAddress(addressUser);
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
    private void updateAddress(String address) {

        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        DocumentReference documentReference = mStore.collection("clients").document(userID);

        Map<String, Object> update = new HashMap<>();
        update.put("Address", address);
        documentReference.update(update).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "address number update" + userID);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "address number fail" + userID);
            }
        });
    }

    public void showUserData(){

        Intent intent = getIntent();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("clients").document(userID);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // DocumentSnapshot data may be null if the document doesn't exist
                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                            // Access specific fields
                            String firstnameUser = documentSnapshot.getString("First Name");
                            String lastnameUser = documentSnapshot.getString("Last Name");
                            String phoneUser = documentSnapshot.getString("Phone Number");
                            String addressUser = documentSnapshot.getString("Address");

                            // Display the fields
                            editFirstName.setText(firstnameUser);
                            editLastName.setText(lastnameUser);
                            editPhone.setText(phoneUser);
                            editAddress.setText(addressUser);

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });

    }
}