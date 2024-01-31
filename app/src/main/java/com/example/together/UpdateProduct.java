package com.example.together;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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

public class UpdateProduct extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    ArrayList<String> productTitleList = new ArrayList<>();
    AutoCompleteTextView aCSelectProduct;
    ArrayAdapter<String> adapterProduct;
    String product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);
        getSupportActionBar().setTitle("עדכון מוצר");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        getProductTitles();


        aCSelectProduct = findViewById(R.id.choose_product_name);

        adapterProduct = new ArrayAdapter<String>(this, R.layout.list_of_proudcts, productTitleList);
        FirebaseApp.initializeApp(this);


        aCSelectProduct.setAdapter(adapterProduct);


        aCSelectProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                product = adapterView.getItemAtPosition(position).toString();
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
                                }
                            }
                            // Now, productTitleList contains all "productTitle" values
                            for (String productTitle : productTitleList) {
                                Log.d(TAG, "Product Title: " + productTitle);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}