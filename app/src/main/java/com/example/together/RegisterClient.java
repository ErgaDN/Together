package com.example.together;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RegisterClient extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextFirstName, editTextLastName, editTextPhone; //, editTextAddress;
    Button buttonReg;
    FirebaseAuth mAuth;
    FirebaseFirestore mStore;
    ProgressBar progressBar;
    TextView textView;
    String userID;
    String address;
    AutoCompleteTextView autoCompleteSelectAddress;
    ArrayAdapter<String> adapterAddress;
    ArrayList<String> addressList = new ArrayList<>();
    FirebaseFirestore db;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), ClientProfile.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_client);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextFirstName = findViewById(R.id.firstName);
        editTextLastName = findViewById(R.id.lastName);
        editTextPhone = findViewById(R.id.phoneNumber);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        mStore = FirebaseFirestore.getInstance();

        getAddressArray();


        autoCompleteSelectAddress = findViewById(R.id.select_address);

        adapterAddress = new ArrayAdapter<String>(this, R.layout.list_of_address, addressList);
        FirebaseApp.initializeApp(this);


        autoCompleteSelectAddress.setAdapter(adapterAddress);


        autoCompleteSelectAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                address = adapterView.getItemAtPosition(position).toString();
            }
        });


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });


        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Button clicked!");
                progressBar.setVisibility(View.VISIBLE);
                String email, password, firstName, lastName, phoneNumber;//, address;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                firstName = String.valueOf(editTextFirstName.getText());
                lastName = String.valueOf(editTextLastName.getText());
                phoneNumber = String.valueOf(editTextPhone.getText());


                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(firstName) ||
                        TextUtils.isEmpty(lastName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(address)) {
                    Toast.makeText(RegisterClient.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterClient.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    userID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                    DocumentReference documentReference = mStore.collection("clients").document(userID);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("First Name", firstName);
                                    user.put("Last Name", lastName);
                                    user.put("Phone Number", phoneNumber);
                                    user.put("Address", address);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "Firestore data inserted successfully.");
                                            Log.d(TAG, "user created" + userID);
                                            Intent intent = new Intent(getApplicationContext(), ClientProfile.class); // TODO: change it to the view of client
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                } else {
                                    Log.e(TAG, "Authentication failed.", task.getException());
                                    Toast.makeText(RegisterClient.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });
    }

    private void getAddressArray() {
        db = FirebaseFirestore.getInstance();
        db.collection("centralizers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Check if the "Address" field exists in the document
                                if (document.contains("Address")) {
                                    String address = document.getString("Address");
                                    addressList.add(address);
                                    System.out.println("add:"+address);
                                }
                            }
                            // Now, addressesList contains all "Address" values
                            for (String address : addressList) {
                                Log.d(TAG, "Address: " + address);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }








}