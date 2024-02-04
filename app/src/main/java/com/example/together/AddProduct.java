package com.example.together;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import static android.content.ContentValues.TAG;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class AddProduct extends AppCompatActivity {

    private EditText titleEt, descriptionEt, quantity, price;
    private TextView category;
    private Button btn_addproduct;

    private FirebaseAuth firebaseAuth;
    FirebaseFirestore mStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        titleEt = findViewById(R.id.titleEt);
        descriptionEt = findViewById(R.id.descriptionEt);
        category = findViewById(R.id.category);
        quantity = findViewById(R.id.quantity);
        price = findViewById(R.id.price);
        btn_addproduct = findViewById(R.id.btn_addproduct);
        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick category
                categoryDialog();
            }
        });

        btn_addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private String productTitle, productDescription, productCategory, productQuantity, originalPrice;
    private void inputData() {
        productTitle = titleEt.getText().toString().trim();
        productDescription = descriptionEt.getText().toString().trim();
        productCategory = category.getText().toString().trim();
        productQuantity = quantity.getText().toString().trim();
        originalPrice = price.getText().toString().trim();

        if (TextUtils.isEmpty(productTitle)){
            Toast.makeText(this, "Title required..", Toast.LENGTH_SHORT).show();
            return; //don't proceed further
        } if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category required..", Toast.LENGTH_SHORT).show();
            return; //don't proceed further
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price required..", Toast.LENGTH_SHORT).show();
            return; //don't proceed further
        }

        addProduct();
    }

    private void addProduct() {

        //add data to db
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> productData = new HashMap<>();
        productData.put("productId", "" + timestamp);
        productData.put("productTitle", "" + productTitle);
        productData.put("productDescription", "" + productDescription);
        productData.put("productCategory", "" + productCategory);
        productData.put("productQuantity", "" + productQuantity);
        productData.put("productPrice", "" + originalPrice);
        productData.put("timestamp", "" + timestamp);
        productData.put("uid", "" + firebaseAuth.getUid());
        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        CollectionReference productCollectionRef = mStore.collection("seller").document(userID).collection("products");

        // Add the sample product document to the "product" collection
        productCollectionRef.add(productData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Product added to the 'products' collection under user: " + userID);

                Intent intent = new Intent(getApplicationContext(), Seller.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void categoryDialog() {
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Product Category").setItems(Constants.productCategories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //set picked category
                String selectedCategory = Constants.productCategories[which];
                //set picked category
                category.setText(selectedCategory);
            }
        }).show();
    }


}