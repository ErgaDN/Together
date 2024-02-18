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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetailsSeller extends AppCompatActivity {

    String orderId;
    ImageButton backBtn, editBtn;
    TextView orderIdTv, dateTv, orderStatusTv, phoneTv, totalItemsTv, amountTv;
    RecyclerView itemsRv;
    String newStatus;

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
//       // setup data to put in db
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("orderStatus", ""+selectedOption);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        String userID = user.getUid();
//        DocumentReference docRef = db.collection("seller").document(userID).collection("orders").document(orderId);
//        Log.d("Debug", "!seller orderid doc ref: " + docRef);
//
//        docRef.update(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//                orderStatusTv.setText(selectedOption);
//                Log.d(TAG, "status update" + userID);
//                Log.d("Debug", "status update" + userID);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Log.e(TAG, "status fail" + userID);
//                Log.d("Debug", "fail status update" + userID);
//            }
//        });
//    }

//    private void editOrderStatus(String selectedOption) {
//        // setup data to put in db
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("orderStatus", selectedOption);
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if (user != null) {
//            String userID = user.getUid();
//            if (userID != null && !userID.isEmpty() && orderId != null && !orderId.isEmpty()) {
//                DocumentReference docRef = db.collection("seller").document(userID).collection("orders").document();
//                Log.d("Debug", "UserID: " + userID);
//                Log.d("Debug", "OrderID: " + orderId);
//                Log.d("Debug", "DocRef: " + docRef.getPath());
//
//                docRef.update(hashMap)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                // Update UI with the new order status
//                                orderStatusTv.setText(selectedOption);
//                                Log.d(TAG, "Status update: " + userID);
//                                Log.d("Debug", "Status update: " + userID);
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.e(TAG, "Status update failed: " + userID);
//                                Log.d("Debug", "Failed status update: " + userID);
//                            }
//                        });
//            } else {
//                Log.e(TAG, "userID or orderId is null or empty");
//            }
//        } else {
//            Log.e(TAG, "Current user is null");
//        }
//    }

    //TODO: need to see it on screen
    private void editOrderStatus(String selectedOption) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userID = user.getUid();
            if (userID != null && !userID.isEmpty() && orderId != null && !orderId.isEmpty()) {
                // Construct a query to find the document with matching orderId
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
                                    Log.d("Debug", "UserID: " + userID);
                                    Log.d("Debug", "OrderID: " + orderId);
                                    Log.d("Debug", "DocRef: " + docRef.getPath());

                                    // Update order status
                                    updateOrderStatus(docRef, selectedOption);
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
        DocumentReference docRef = db.collection("seller").document(userID).collection("orders").document(orderId);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // DocumentSnapshot data may be null if the document doesn't exist
                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                            // Access specific fields
                            String phoneUser = documentSnapshot.getString("phoneClient");

                            // Display the fields
                            phoneTv.setText(phoneUser);

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

    private void loadOrderDetails() {
        //load detail info of this order, based on orderId
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userID = user.getUid();
        DocumentReference docRef = db.collection("seller").document(userID).collection("orders").document(orderId);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // DocumentSnapshot data may be null if the document doesn't exist
                            Log.d(TAG, "DocumentSnapshot data: " + documentSnapshot.getData());

                            // Access specific fields
                            String phoneUser = documentSnapshot.getString("phoneClient");
                            String nameClient = documentSnapshot.getString("nameClient");
                            String orderStatus = documentSnapshot.getString("orderStatus");
                            String productId = documentSnapshot.getString("productId");
                            String productPrice = documentSnapshot.getString("productPrice");
                            String productPriceEach = documentSnapshot.getString("productPriceEach");
                            String productQuantity = documentSnapshot.getString("productQuantity");
                            String productTitle = documentSnapshot.getString("productTitle");
                            String orderId = documentSnapshot.getString("orderId");

                            //change order status text color
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
//                            dateTv.setText();


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
                                totalItemsTv.setText("1");//TODO:change it 37:30 (17)
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