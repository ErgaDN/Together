package com.example.together.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.together.R;
import com.example.together.adapters.AdapterOrderedItem;
import com.example.together.models.ModelOrderItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class OrderDetailsClient extends AppCompatActivity {

    private String orderId; // orderTo;

    //ui views
    private TextView orderIdTv, dateTv, orderStatusTv, costTv, addressTv;
    private RecyclerView itemsRv;
    private FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_client);
        getSupportActionBar().setTitle("היסטוריית הזמנות");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init views
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        costTv = findViewById(R.id.costTv);
        addressTv = findViewById(R.id.addressTv);
        itemsRv = findViewById(R.id.itemsRv);
        db = FirebaseFirestore.getInstance();


        Intent intent = getIntent();
//        orderTo = intent.getStringExtra("orderTo");
        orderId = intent.getStringExtra("orderId");

        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        loadOrderDetails();
        loadOrderItem();
    }

    private void loadOrderDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
//        String userId = user.getUid();
        CollectionReference docRef = db.collection("clients").document(userId).collection("orders");

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { // Change to QuerySnapshot
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        // Check if there are any documents
                        if (!querySnapshot.isEmpty()) {
                            // Access the first document
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);

                            // Access specific fields
                            String orderId = documentSnapshot.getString("orderId");
                            String orderDate = documentSnapshot.getString("orderDate");
                            String orderStatus = documentSnapshot.getString("orderStatus");
                            String addressToDelivery = documentSnapshot.getString("address to delivery");
                            String orderCost = documentSnapshot.getString("orderCost");

                            // Change order status text color
                            updateColorStatus(orderStatus);

                            // Display the fields
                            orderIdTv.setText(orderId);
                            dateTv.setText(orderDate);
//                            orderStatusTv.setText(orderStatus);
                            addressTv.setText(addressToDelivery);
                            costTv.setText(orderCost);

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });
    }

    private void updateColorStatus(String selectedOption) {
        // Change order status text and color
        orderStatusTv.setText(selectedOption);

        // Change order status text color
        switch (selectedOption) {
            case "בתהליך":
                orderStatusTv.setTextColor(getResources().getColor(R.color.lavender));
                break;
            case "הושלמה":
                orderStatusTv.setTextColor(getResources().getColor(R.color.green));
                break;
            case "בוטלה":
                orderStatusTv.setTextColor(getResources().getColor(R.color.red));
                break;
        }
    }

    private ArrayList<ModelOrderItem> orderItemList;
    private AdapterOrderedItem adapterOrderedItem;

    private void loadOrderItem() {
        orderItemList = new ArrayList<>();

        DocumentReference clientRef = db.collection("clients").document(userId);

        clientRef.collection("orders")
                .whereEqualTo("orderId", orderId) // Filter orders by orderId
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        orderItemList.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot orderDocument : task.getResult()) {

                                // Now, within the filtered order document, query the "products" sub-collection
                                orderDocument.getReference().collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> productsTask) {
                                        if (productsTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot productDocument : productsTask.getResult()) {
                                                //get information from the product document
                                                String pId = productDocument.getString("pId");
                                                String name = productDocument.getString("name");
                                                String cost = productDocument.getString("cost");
                                                String price = productDocument.getString("price");
                                                String quantity = productDocument.getString("quantity");

                                                // Create ModelOrderItem object
                                                ModelOrderItem modelOrderItem = new ModelOrderItem(
                                                        pId,
                                                        name,
                                                        cost,
                                                        price,
                                                        quantity);
                                                orderItemList.add(modelOrderItem);
                                            }
                                            // Setup adapter
                                            adapterOrderedItem = new AdapterOrderedItem(OrderDetailsClient.this, orderItemList);
                                            // Set adapter
                                            itemsRv.setAdapter(adapterOrderedItem);
                                        } else {
                                            Log.d("Debug", "Error getting products: ", productsTask.getException());
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("Debug", "Error getting orders: ", task.getException());
                        }
                    }
                });

    }
}