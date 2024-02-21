package com.example.together.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.together.Constants;
import com.example.together.R;
import com.example.together.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddProduct extends AppCompatActivity {
    public static final int PICK_IMAGE_REQUEST = 1;

    private EditText titleEt, descriptionEt, quantity, price;
    private TextView category;
    private Button btn_addproduct, btn_choose_image;
    private ImageView productIconIv;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mStore;
    private Uri imageUri;

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
        btn_choose_image = findViewById(R.id.btn_choose_image);
        productIconIv = findViewById(R.id.productIconIv);
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

        btn_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // choose image
                chooseImage();
            }
        });

        btn_addproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            productIconIv.setImageURI(imageUri);
        }
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
        }
        if (TextUtils.isEmpty(productCategory)){
            Toast.makeText(this, "Category required..", Toast.LENGTH_SHORT).show();
            return; //don't proceed further
        }
        if (TextUtils.isEmpty(originalPrice)){
            Toast.makeText(this, "Price required..", Toast.LENGTH_SHORT).show();
            return; //don't proceed further
        }
        if (imageUri == null) {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
            return; //don't proceed further
        }

        uploadProduct();
    }

    private void uploadProduct() {
        // Convert image to base64
        Bitmap bitmap = ImageHelper.getBitmapFromUri(this, imageUri);
        String productIcon = ImageHelper.encodeImageBase64(bitmap);

        // Get current user ID
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        String timestamp = "" + System.currentTimeMillis();

        // Create a new product object
        ModelProduct product = new ModelProduct(timestamp, productTitle, productDescription, productCategory, Long.parseLong(productQuantity), productIcon, originalPrice, timestamp, userID);
            Log.d("DEBUG", "productIcon of " + product.getProductTitle() +  " is " + product.getProductIcon());


        // Add product to Firestore
        CollectionReference productCollectionRef = mStore.collection("seller").document(userID).collection("products");
        productCollectionRef.add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("DEBUG", "productIcon of " + product.getProductTitle() +  " is " + product.getProductIcon());
                        Log.d("TAG", "Product added to Firestore");
                        Toast.makeText(AddProduct.this, "Product added", Toast.LENGTH_SHORT).show();

                        // Navigate back to Seller activity
                        Intent intent = new Intent(getApplicationContext(), Seller.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProduct.this, "Failed to add product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
