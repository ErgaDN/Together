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

    EditText editFirstName, editLastName, editphone, editEmail, editPassword;
    Button saveButton;
    String firstNameUser, lastNameUser, phoneUser, emailUser, passwordUser,usernameUser;
//    DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore mStore;

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

        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        showData();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFifstNameChanged() ) { //|| isLastNameChanged() || isPhoneChanged()
                    Toast.makeText(clientEditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(clientEditProfile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public boolean isFifstNameChanged(){
//        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
//        DocumentReference documentReference = mStore.collection("clients").document(userID);
//
//        documentReference.get()
//                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                    @Override
//                    public void onSuccess(DocumentSnapshot documentSnapshot) {
//                        if (documentSnapshot.exists()) {
//                            String firstName = documentSnapshot.getString("First Name");
//                            if (firstName != null) {
//                                // Store the retrieved "First Name" in the 'firstName' variable
//                                firstNameUser = firstName;
//
//                                // Now you can use the 'storedFirstName' variable in your code
//                                Log.d(TAG, "Stored First Name: " + firstNameUser);
//
//                                // For example, update a UI element with this stored value
//                                // textViewFirstName.setText(storedFirstName);
//                            } else {
//                                Log.d(TAG, "First Name is null");
//                                // Handle the case where "First Name" is not present in the document
//                            }
//                        } else {
//                            Log.d(TAG, "Document does not exist");
//                            // Handle the case where the document does not exist
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.w(TAG, "Error getting document", e);
//                        // Handle any errors that occurred while fetching the document
//                    }
//                });

//        if (!firstNameUser.trim().equals(editFirstName.getText().toString())){//
        if (editFirstName != null && editFirstName.length() > 0){//
            firstNameUser= editFirstName.getText().toString();
            updateFirstName(firstNameUser);
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
        update.put("Fist Name", firstName);
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

//    public boolean isLastNameChanged(){
//        if (!lastNameUser.equals(editLastName.getText().toString())){
//            reference.child(usernameUser).child("Last Name").setValue(editLastName.getText().toString());
//            lastNameUser = editLastName.getText().toString();
//            return true;
//        } else{
//            return false;
//        }
//    }
//    public boolean isPhoneChanged(){
//        if (!phoneUser.equals(editphone.getText().toString())){
//            reference.child(usernameUser).child("Phone Number").setValue(editphone.getText().toString());
//            phoneUser = editphone.getText().toString();
//            return true;
//        } else{
//            return false;
//        }
//    }
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