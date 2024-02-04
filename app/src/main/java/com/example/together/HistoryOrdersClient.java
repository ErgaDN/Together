package com.example.together;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class HistoryOrdersClient extends AppCompatActivity {
    private RecyclerView orderRv;
    private ArrayList<ModelOrderClient> orderList;

    private AdapterOrderClient adapterOrderClient;

    FirebaseFirestore db;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_orders_client);

        orderRv = findViewById(R.id.orderRv);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

//        FirebaseApp.initializeApp(this);

        loadOrders();

    }

    //go over the orders collection and get the product collection
    private void loadOrders() {
        // Initialize order list
        orderList = new ArrayList<>();
        FirebaseUser user = mAuth.getCurrentUser();
        String userID = user.getUid();

        CollectionReference ordersRef = db.collection("clients").document(userID).collection("orders");
        Toast.makeText(HistoryOrdersClient.this, "get ordersRef"+ordersRef, Toast.LENGTH_SHORT).show();

        ordersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot orderDocument : task.getResult()) {
                        String orderId = orderDocument.getId();

                        // Assuming this is the correct type for productRef
                        CollectionReference productRef = ordersRef.document(orderId).collection("products");

                        productRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> productTask) {
                                if (productTask.isSuccessful()) {
                                    for (QueryDocumentSnapshot productDocument : productTask.getResult()) {
                                        String productTitle = productDocument.getString("name");
                                        Toast.makeText(HistoryOrdersClient.this, "productTitle" + productTitle, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

    }
//    private void loadOrders() {
//        // Initialize order list
//        orderList = new ArrayList<>();
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userID = user.getUid();
//
//        DocumentReference clientRef = db.collection("clients").document(userID);
//        Toast.makeText(HistoryOrdersClient.this, "get clientRef", Toast.LENGTH_SHORT).show();
//
//        // Get orders
//        clientRef.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                orderList.clear();
//                Toast.makeText(HistoryOrdersClient.this, "onComplete orders", Toast.LENGTH_SHORT).show();
//                if (task.isSuccessful()) {
//                    Toast.makeText(HistoryOrdersClient.this, "on the if", Toast.LENGTH_SHORT).show();
//
//                    for (QueryDocumentSnapshot orderDocument : task.getResult()) {
//                        Toast.makeText(HistoryOrdersClient.this, "on the for", Toast.LENGTH_SHORT).show();
//
//                        String orderId = orderDocument.getId();
//
//                        // Check if the order document exists
//                        if (orderDocument.exists()) {
//                            CollectionReference productsRef = clientRef.collection("orders").document(orderId).collection("products");
//                            Toast.makeText(HistoryOrdersClient.this, "get productsRef", Toast.LENGTH_SHORT).show();
//
//
//                        } else {
//                            // Handle the case where the order document doesn't exist
//                            Toast.makeText(HistoryOrdersClient.this, "Order document with ID " + orderId + " does not exist", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        });
//    }


//    private void loadOrders() {
//        //init order list
//        orderList = new ArrayList<>();
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userID = user.getUid();
//
//        DocumentReference clientRef = db.collection("clients").document(userID);
//        Toast.makeText(HistoryOrdersClient.this, "get clientRef", Toast.LENGTH_SHORT).show();
//
//        //get orders
//        clientRef.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                orderList.clear();
//                Toast.makeText(HistoryOrdersClient.this, "onComplete orders", Toast.LENGTH_SHORT).show();
//                if (task.isSuccessful()) {
//                    // Iterate over each order document
//                    Toast.makeText(HistoryOrdersClient.this, "on the if", Toast.LENGTH_SHORT).show();
//
//                    for (QueryDocumentSnapshot orderDocument : task.getResult()) {
//                        Toast.makeText(HistoryOrdersClient.this, "on the for", Toast.LENGTH_SHORT).show();
//
//                        String orderId = orderDocument.getId();
//
//                        // Access the "products" sub-collection inside the order document
//                        CollectionReference productsRef = clientRef.collection("orders").document(orderId).collection("products");
//                        Toast.makeText(HistoryOrdersClient.this, "get productsRef", Toast.LENGTH_SHORT).show();
//
//                        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    Toast.makeText(HistoryOrdersClient.this, "productsRef if", Toast.LENGTH_SHORT).show();
//
//                                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
//                                        Toast.makeText(HistoryOrdersClient.this, "productsRef for", Toast.LENGTH_SHORT).show();
//
////                                        ModelOrderClient modelOrderClient = productDocument.toObject(ModelOrderClient.class);
////                                        orderList.add(modelOrderClient);
//                                    }
////                                    adapterOrderClient = new AdapterOrderClient(HistoryOrdersClient.this, orderList);
////                                    orderRv.setAdapter(adapterOrderClient);
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
//    }

//
//    private void loadOrders() {
//        //init order list
//        orderList = new ArrayList<>();
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userID = user.getUid();
//
//        DocumentReference clientRef = db.collection("clients").document(userID);
//
//        //get orders
//        clientRef.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                orderList.clear();
//                if (task.isSuccessful()) {
//                    // Iterate over each order document
//                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
//                        String orderId = productDocument.getId();
//
//                        // Access the "products" subcollection inside the order document
//                        CollectionReference productsRef = clientRef.collection("orders").document(orderId).collection("products");
//
//
//                        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
//
//                                        ModelOrderClient modelOrderClient = productDocument.toObject(ModelOrderClient.class);
//                                        orderList.add(modelOrderClient);
//                                    }
//                                    adapterOrderClient = new AdapterOrderClient(HistoryOrdersClient.this, orderList);
//                                    orderRv.setAdapter(adapterOrderClient);
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
//    }

}