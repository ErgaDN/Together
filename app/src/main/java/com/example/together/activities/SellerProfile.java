package com.example.together.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.together.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SellerProfile extends AppCompatActivity {
    TextView profilefirstname, profilelastname, profilephone, profileaddress;
    FirebaseAuth mAuth;
    Button editProfile, button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        getSupportActionBar().setTitle("אזור אישי");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editProfile = findViewById(R.id.editButton);
        profilefirstname = findViewById(R.id.profileFirstName);
        profilelastname = findViewById(R.id.profileLastName);
        profilephone = findViewById(R.id.profilePhoneNumber);
        profileaddress = findViewById(R.id.profileAddress);
        mAuth = FirebaseAuth.getInstance();

        showUserData();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passUserData();
            }
        });

    }

    public void showUserData() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("seller").document(userID);

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
                            profilefirstname.setText(firstnameUser);
                            profilelastname.setText(lastnameUser);
                            profilephone.setText(phoneUser);
                            profileaddress.setText(addressUser);

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

    public void passUserData(){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("seller").document(userID);
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

                            Intent intent = new Intent(SellerProfile.this, sellerEditProfile.class);

                            // Display the fields
                            profilefirstname.setText(firstnameUser);
                            profilelastname.setText(lastnameUser);
                            profilephone.setText(phoneUser);
                            profileaddress.setText(addressUser);

                            startActivity(intent);

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