package com.example.together.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.together.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChooseUpdateProduct extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<String> productTitleList = new ArrayList<>();
    AutoCompleteTextView aCSelectProduct;
    ArrayAdapter<String> adapterProduct;
    String selectedProduct;
    String productId;
    Map<String, String> productTitleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_update_product);
        getSupportActionBar().setTitle("עדכון מוצר");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        productTitleMap = new HashMap<>();

        getProductTitles();

        aCSelectProduct = findViewById(R.id.choose_product_name);
        adapterProduct = new ArrayAdapter<String>(this, R.layout.list_of_proudcts, productTitleList);
        FirebaseApp.initializeApp(this);
        aCSelectProduct.setAdapter(adapterProduct);
        aCSelectProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedProduct = adapterView.getItemAtPosition(position).toString();
                // Access the productId using the map
                productId = productTitleMap.get(selectedProduct);
                Intent intent = new Intent(ChooseUpdateProduct.this, UpdateProduct.class);
                intent.putExtra("IDproduct", productId);
                startActivity(intent);
            }
        });

    }

    private void getProductTitles() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("seller").document(userID);

        docRef.collection("products")  // Access the "products" sub-collection
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot productDocument : task.getResult()) {
                                // Check if the "productTitle" field exists in the product document
                                if (productDocument.contains("productTitle")) {
                                    String productTitle = productDocument.getString("productTitle");
                                    productTitleList.add(productTitle);
                                    String productId = productDocument.getString("productId");
                                    productTitleMap.put(productTitle, productId);

                                }
                            }
                            // Now, productTitleList contains all "productTitle" values
                            adapterProduct.notifyDataSetChanged(); // Notify the adapter about the data change
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}

