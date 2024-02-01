package com.example.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class Client extends AppCompatActivity {
    ImageButton btn_profile, btn_client_cart, btn_logout, filterProductBtn;
    Toolbar toolbar;
    private EditText searchProductsEt;
    private TextView filteredProductsTv;
    private RecyclerView productsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private AdapterProductClient adapterProductClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        btn_profile = findViewById(R.id.btn_profile);
        btn_client_cart = findViewById(R.id.btn_client_cart);
        btn_logout = findViewById(R.id.btn_logout);
        searchProductsEt = findViewById(R.id.searchProductsEt);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        productsRv = findViewById(R.id.productsRv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadProducts();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Client.this, ClientProfile.class);
                startActivity(intent);
                finish();
            }
        });

        btn_client_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Client.this, ClientCart.class);
                startActivity(intent);
                finish();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Client.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        

    }

    private void loadProducts() {

        productList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Create a reference to the "sellers" collection
        CollectionReference sellersRef = db.collection("seller");

        sellersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                productList.clear();
                if (task.isSuccessful()) {
                    // Iterate over each seller document
                    for (QueryDocumentSnapshot sellerDocument : task.getResult()) {
                        String sellerId = sellerDocument.getId();

                        // Access the "products" subcollection inside the seller document
                        CollectionReference productsRef = sellersRef.document(sellerId).collection("products");

                        // Now you can perform operations on the "products" subcollection for this seller
                        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
                                        ModelProduct modelProduct = productDocument.toObject(ModelProduct.class);
                                        productList.add(modelProduct);
                                    }
                                    adapterProductClient = new AdapterProductClient(Client.this, productList);
                                    productsRv.setAdapter(adapterProductClient);
                                }
                            }
                        });
                    }
                }
            }
        });

//        //get all product
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("seller");
//        reference.child("products")
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@Nonnull DataSnapshot dataSnapshot) {
//                        //before getting rest list
//                        productList.clear();
//                        for (DataSnapshot ds: dataSnapshot.getChildren()) {
//                            ModelProduct modelProduct = ds.getValue(ModelProduct.class);
//                            productList.add(modelProduct);
//                        }
//                        //setup adapter
//                        adapterProductClient = new AdapterProductClient(Client.this, productList);
//                        //set adapter
//                        productsRv.setAdapter(adapterProductClient);
//                    }
//
//                    @Override
//                    public void onCancelled(@Nonnull DatabaseError databaseError) {
//
//                    }
//                });

    }
}