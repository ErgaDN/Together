package com.example.together.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OrderDetailsSeller extends AppCompatActivity {

    String orderId;
    ImageButton backBtn, editBtn;
    TextView orderIdTv, dateTv, orderStatusTv, phoneTv, totalItemsTv, amountTv;
    RecyclerView itemsRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderItem> orderItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        //get data from intent
        orderId = getIntent().getStringExtra("orderId");

        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        phoneTv = findViewById(R.id.phoneTv);
        totalItemsTv = findViewById(R.id.totalItemsTv);
        amountTv = findViewById(R.id.amountTv);
        itemsRv = findViewById(R.id.itemsRv);

        firebaseAuth = FirebaseAuth.getInstance();

        loadBuyerInfo();
        loadOrderDetails();
        loadOrderItems();


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back
                onBackPressed();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit order status
                editOrderStatusDialog();
            }
        });

    }

    private void editOrderStatusDialog() {
        //options to display in dialog
            String[] option = {"בתהליך", "בוטלה", "הושלמה"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("עריכת סטטוס הזמנה").setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle items click
                String selectedOption = option[which];
                editOrderStatus(selectedOption);

            }

        }).show();

    }


//    private void editOrderStatus(String selectedOption) {
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            String userID = user.getUid();
//            if (!userID.isEmpty() && orderId != null && !orderId.isEmpty()) {
//                // Construct a query to find the document with matching orderId
//                db.collection("seller").document(userID).collection("orders")
//                        .whereEqualTo("orderId", orderId)
//                        .get()
//                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                            @Override
//                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                                if (!queryDocumentSnapshots.isEmpty()) {
//                                    // There should be only one document with matching orderId
//                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
//                                    DocumentReference docRef = documentSnapshot.getReference();
//                                    Log.d("Debug", "UserID: " + userID);
//                                    Log.d("Debug", "OrderID: " + orderId);
//                                    Log.d("Debug", "DocRef: " + docRef.getPath());
//
//                                    if (Objects.equals(selectedOption, "הושלמה")) {
//                                        updateProductsQuantity();
//                                    }
//
//                                    // Update order status
//                                    updateOrderStatus(docRef, selectedOption);
//                                } else {
//                                    Log.e(TAG, "No document found with matching orderId");
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e(TAG, "Error retrieving document", e);
//                            }
//                        });
//            } else {
//                Log.e(TAG, "userID or orderId is null or empty");
//            }
//        } else {
//            Log.e(TAG, "Current user is null");
//        }
//    }

    private void editOrderStatus(String selectedOption) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            if (!userID.isEmpty() && orderId != null && !orderId.isEmpty()) {
                // Construct a query to find the document with matching orderId in the seller's collection
                db.collection("seller").document(userID).collection("orders")
                        .whereEqualTo("orderId", orderId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    // There should be only one document with matching orderId
                                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                    DocumentReference docRef = documentSnapshot.getReference();

                                    if (Objects.equals(selectedOption, "הושלמה")) {
                                        updateProductsQuantity();
                                    }

                                    // Update order status in the seller
                                    updateOrderStatus(docRef, selectedOption);

                                    // Extract clientId and orderId from the seller's order document
                                    String clientId = documentSnapshot.getString("clientId");
                                    String orderId = documentSnapshot.getString("orderId");

                                    // Construct a query to find the client's order document
                                    Query clientOrderQuery = db.collection("clients")
                                            .document(clientId)
                                            .collection("orders")
                                            .whereEqualTo("orderId", orderId);

                                    // Execute the query to find the client's order document
                                    clientOrderQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                // There should be only one document with matching orderId
                                                DocumentSnapshot clientOrderDocument = queryDocumentSnapshots.getDocuments().get(0);
                                                DocumentReference clientOrderRef = clientOrderDocument.getReference();

                                                // Update order status in the client's collection
                                                updateOrderStatus(clientOrderRef, selectedOption);
                                            } else {
                                                Log.e(TAG, "No document found in the client's collection with matching orderId");
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG, "Error querying client's collection", e);
                                        }
                                    });

                                } else {
                                    Log.e(TAG, "No document found with matching orderId");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error retrieving document", e);
                            }
                        });
            } else {
                Log.e(TAG, "userID or orderId is null or empty");
            }
        } else {
            Log.e(TAG, "Current user is null");
        }
    }

    private void updateProductsQuantity() {
        //TODO

    }

    private void updateOrderStatus(DocumentReference docRef, String selectedOption) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("orderStatus", selectedOption);

        docRef.update(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // Update UI with the new order status
                        orderStatusTv.setText(selectedOption);
                        Log.d(TAG, "Status update successful");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Status update failed", e);
                    }
                });
    }


    private void loadBuyerInfo() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();

        // Query the collection to find the document with the matching orderId
        db.collection("seller").document(userID).collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // There should be only one document with matching orderId
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            // Access specific fields
                            String phoneUser = documentSnapshot.getString("phoneClient");
                            // Display the fields
                            phoneTv.setText(phoneUser);
                        } else {
                            Log.d(TAG, "No document found with matching orderId");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving document", e);
                    }
                });
    }


    private void loadOrderDetails() {
        // Load detailed information of this order based on orderId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();

        // Query the collection to find the document with the matching orderId
        db.collection("seller").document(userID).collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // There should be only one document with matching orderId
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Access specific fields
                            String phoneUser = documentSnapshot.getString("phoneClient");
                            String nameClient = documentSnapshot.getString("nameClient");
                            String orderStatus = documentSnapshot.getString("orderStatus");
//                            String productId = documentSnapshot.getString("productId");
//                            String productPrice = documentSnapshot.getString("productPrice");
                            String productPriceEach = documentSnapshot.getString("productPriceEach");
//                            String productQuantity = documentSnapshot.getString("productQuantity");
//                            String productTitle = documentSnapshot.getString("productTitle");
                            String orderId = documentSnapshot.getString("orderId");
                            String orderDate = documentSnapshot.getString("orderDate");

                            // Change order status text color
                            if (orderStatus.equals("בתהליך")) {
                                orderStatusTv.setTextColor(getResources().getColor(R.color.lavender));
                            } else if (orderStatus.equals("הושלמה")) {
                                orderStatusTv.setTextColor(getResources().getColor(R.color.green));
                            } else if (orderStatus.equals("בוטלה")) {
                                orderStatusTv.setTextColor(getResources().getColor(R.color.red));
                            }

                            // Display the fields
                            phoneTv.setText(phoneUser);
                            orderIdTv.setText(orderId);
                            orderStatusTv.setText(orderStatus);
                            dateTv.setText(orderDate);
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


    private void loadOrderItems() {
        orderItemArrayList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();


        String userId = user.getUid();
        DocumentReference sellerRef = db.collection("seller").document(userId);

        sellerRef.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();

                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // Iterate over each document in the "orders" collection
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    ModelOrderItem modelOrderItem = document.toObject(ModelOrderItem.class);
                                    orderItemArrayList.add(modelOrderItem);
                                }
                                adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSeller.this, orderItemArrayList);
                                itemsRv.setAdapter(adapterOrderedItem);

                                //set total items in orders
                                totalItemsTv.setText(String.valueOf(querySnapshot.size()));
                            } else {
                                // Handle the case where the "orders" collection is empty
                            }
                        } else {
                            // Handle potential errors here
                            Exception exception = task.getException();
                            if (exception != null) {
                                // Log or handle the exception
                            }
                        }
                    }
                });

    }
}