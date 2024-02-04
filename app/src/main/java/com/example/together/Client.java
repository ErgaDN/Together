package com.example.together;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class Client extends AppCompatActivity {
    ImageButton btn_profile, btn_client_cart, btn_logout, filterProductBtn;
    Toolbar toolbar;
    FirebaseFirestore db;
    private EditText searchProductsEt;
    private TextView filteredProductsTv, cartCountTv;
    private RecyclerView productsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private AdapterProductClient adapterProductClient;
    private String userId;
    private int count;

    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;

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
        cartCountTv = findViewById(R.id.cartCountTv);

        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        loadProducts();
        cartCount();

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
                showCartDialog();
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

    public double allTotalPrice = 0.0;
    public RecyclerView cardItemRv;
    public TextView sTotalTv;
    public Button checkoutBtn;

    //need to access these views in adapter so making public
    private void showCartDialog() {
        //init list
        cartItemList = new ArrayList<>();
        allTotalPrice = 0;

        //inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
        cardItemRv = view.findViewById(R.id.cardItemRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);



        DocumentReference docRef = db.collection("clients").document(userId);

        docRef.collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                cartItemList.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot cartDocument : task.getResult()) {
                        //get information
                        String idProduct = cartDocument.getString("uid");
                        String pIdProduct = cartDocument.getString("productId");
                        String nameProduct = cartDocument.getString("productTitle");
                        String priceProduct = cartDocument.getString("productPriceEach");
                        String costProduct = cartDocument.getString("productPrice");
                        String quantityProduct = cartDocument.getString("productQuantity");

                        allTotalPrice += Double.parseDouble(costProduct);

                        // Use toObject to convert the document snapshot to a ModelProduct object
                        ModelCartItem modelCartItem = new ModelCartItem(
                                ""+idProduct,
                                ""+pIdProduct,
                                ""+nameProduct,
                                ""+priceProduct,
                                ""+costProduct,
                                ""+quantityProduct);

                        cartItemList.add(modelCartItem);

                    }
                    //setup adapter
                    adapterCartItem = new AdapterCartItem(Client.this, cartItemList);
                    //set adapter
                    cardItemRv.setAdapter(adapterCartItem);
                    sTotalTv.setText("₪" + allTotalPrice);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void loadProducts() {

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

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
    }

    private void cartCount(){
        DocumentReference docRef = db.collection("clients").document(userId);

        docRef.collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                count=0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot cartDocument : task.getResult()) {
                        //get information
                        String quantityProduct = cartDocument.getString("productQuantity");

                        // Use toObject to convert the document snapshot to a ModelProduct object
                        try {
                            int quantity = Integer.parseInt(quantityProduct);
                            count += quantity;
                        } catch (NumberFormatException e) {
                            // Handle the case where the quantityProduct is not a valid integer
                            Log.e(TAG, "Error parsing quantity as integer", e);
                        }
                    }
                    cartCountTv.setVisibility(View.VISIBLE);
                    cartCountTv.setText(String.valueOf(count));
                    Toast.makeText(Client.this, "quntity"+count, Toast.LENGTH_SHORT).show();
                }else {
                    cartCountTv.setVisibility(View.GONE);
                }
            }
        });
    }

}