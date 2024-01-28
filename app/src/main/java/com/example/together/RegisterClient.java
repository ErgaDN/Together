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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
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

    String[] selectAddress = {"Ariel", "Tel-Aviv"};
    AutoCompleteTextView autoCompleteSelectAddress;
    ArrayAdapter<String> adapterAddress;



//    Query capitalCities = mStore.collection("centralizers");

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), client.class);
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
//        editTextAddress = findViewById(R.id.address);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        mStore = FirebaseFirestore.getInstance();
        autoCompleteSelectAddress = findViewById(R.id.select_address);
        adapterAddress = new ArrayAdapter<String>(this, R.layout.list_of_address, selectAddress);

        autoCompleteSelectAddress.setAdapter(adapterAddress);
        autoCompleteSelectAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                address = adapterView.getItemAtPosition(position).toString();
//                Toast.ma
            }
        });
        textView.setOnClickListener(new View.OnClickListener(){
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
//                address = String.valueOf(editTextAddress.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterClient.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterClient.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(firstName)){
                    Toast.makeText(RegisterClient.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(lastName)){
                    Toast.makeText(RegisterClient.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(RegisterClient.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(address)){
                    Toast.makeText(RegisterClient.this, "Select Address", Toast.LENGTH_SHORT).show();
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
                                            Intent intent = new Intent(getApplicationContext(), client.class); // TODO: change it to the view of client
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
}