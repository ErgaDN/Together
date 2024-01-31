package com.example.together;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UpdateProduct extends AppCompatActivity {
    String value;
    EditText editTitle, editDescription, editQuantity, editPrice;
    String title, category, description, quantity, price;
    TextView editCategory;
    Button btn_updateproduct;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore mStore;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        editTitle = findViewById(R.id.titleEt);
        editDescription = findViewById(R.id.descriptionEt);
        editQuantity = findViewById(R.id.quantity);
        editPrice = findViewById(R.id.price);
        editCategory = findViewById(R.id.category);
        btn_updateproduct = findViewById(R.id.btn_updateproduct);

        firebaseAuth = FirebaseAuth.getInstance();
        mStore = FirebaseFirestore.getInstance();
        db = FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("עדכון מוצר");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //get the IDproduct from the other activity
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            value = extras.getString("IDproduct");
        }

        editCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick category
                categoryDialog();
            }
        });

        btn_updateproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isTitleChanged, isCategoryChanged;
                isTitleChanged = isTitleChanged();
                isCategoryChanged = isCategoryChanged();


                if (isTitleChanged || isCategoryChanged ) {
                    Toast.makeText(UpdateProduct.this, "Saved", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UpdateProduct.this, "No Changes Found", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), ChooseUpdateProduct.class);
                startActivity(intent);
            }
        });




    }

    public boolean isTitleChanged(){
        if (editTitle != null && editTitle.length() > 0){//
            title = editTitle.getText().toString();
            updateTitle(title);
            return true;
        } else{
            return false;
        }
    }

    public boolean isCategoryChanged(){
        if (editCategory != null && editCategory.length() > 0){//
            category = editCategory.getText().toString();
            updateCategory(category);
            return true;
        } else{
            return false;
        }
    }

    private void updateTitle(String title) {
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Reference to the "products" collection
        CollectionReference productsRef = db.collection("seller")
                .document(userID)
                .collection("products");

        // Query to find the document where "productId" matches the given value
        Query query = productsRef.whereEqualTo("productId", value);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Update the "productTitle" field in the found document
                        document.getReference().update("productTitle", title)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Title update successful for user: " + userID);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Title update failed for user: " + userID, e);
                                    }
                                });
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private void updateCategory(String category) {
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Reference to the "products" collection
        CollectionReference productsRef = db.collection("seller")
                .document(userID)
                .collection("products");

        // Query to find the document where "productId" matches the given value
        Query query = productsRef.whereEqualTo("productId", value);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Update the "productTitle" field in the found document
                        document.getReference().update("productCategory", category)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "Category update successful for user: " + userID);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Category update failed for user: " + userID, e);
                                    }
                                });
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
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
                editCategory.setText(selectedCategory);
            }
        }).show();
    }

}