package com.example.together.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.together.Constants;
import com.example.together.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class AddProduct extends AppCompatActivity {

    private EditText titleEt, descriptionEt, quantity, price;
    private TextView category;
    private Button btn_addproduct, btn_add_photo;
    private ImageView productIconIv;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore mStore;
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;
    private Uri mImageUri;

    private static final int PICK_IMAGE_REQUEST = 1;

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
        btn_add_photo = findViewById(R.id.btn_add_photo);
        productIconIv = findViewById(R.id.productIconIv);

        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();
        mStorageRef = mStorage.getReference("product_images");

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

        btn_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            productIconIv.setImageURI(mImageUri);
        }
    }

    private void inputData() {
        String productTitle = titleEt.getText().toString().trim();
        String productDescription = descriptionEt.getText().toString().trim();
        String productCategory = category.getText().toString().trim();
        String productQuantity = quantity.getText().toString().trim();
        String originalPrice = price.getText().toString().trim();

        if (TextUtils.isEmpty(productTitle) || TextUtils.isEmpty(productCategory) || TextUtils.isEmpty(originalPrice)) {
            Toast.makeText(this, "Title, Category, and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageUri == null) {
            Toast.makeText(this, "Please select an image for the product", Toast.LENGTH_SHORT).show();
            return;
        }

        addProduct(productTitle, productDescription, productCategory, productQuantity, originalPrice);
    }

    private void addProduct(String productTitle, String productDescription, String productCategory, String productQuantity, String originalPrice) {
        String sellerID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        String timestamp = String.valueOf(System.currentTimeMillis());

        // Upload image to Firebase Storage
        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");
        fileReference.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL from the StorageReference
                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                // Image uploaded successfully, now add product details to Firestore
                                String imageUrl = uri.toString();

                                // Create a HashMap to store product data
                                HashMap<String, Object> productData = new HashMap<>();
                                productData.put("productId", timestamp);
                                productData.put("productTitle", productTitle);
                                productData.put("productDescription", productDescription);
                                productData.put("productCategory", productCategory);
                                productData.put("productQuantity", productQuantity);
                                productData.put("productPrice", originalPrice);
                                productData.put("productImageUrl", imageUrl);
                                productData.put("timestamp", timestamp);
                                productData.put("uid", firebaseAuth.getUid());
                                productData.put("sellerId", sellerID);

                                // Add the product data to Firestore
                                CollectionReference productCollectionRef = mStore.collection("seller").document(sellerID).collection("products");
                                productCollectionRef.add(productData)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Log.d("AddProduct", "Product added to Firestore with ID: " + documentReference.getId());
                                                Toast.makeText(AddProduct.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AddProduct", "Error adding product to Firestore: " + e.getMessage());
                                            Toast.makeText(AddProduct.this, "Error adding product. Please try again.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
//                    Log.e("AddProduct", "Error uploading image to Firebase Storage: " + e.getMessage());
                    Toast.makeText(AddProduct.this, "Error uploading image. Please try again.", Toast.LENGTH_SHORT).show();
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
                category.setText(selectedCategory);
            }
        }).show();
    }
}
