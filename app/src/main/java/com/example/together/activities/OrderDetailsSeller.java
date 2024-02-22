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
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class OrderDetailsSeller extends AppCompatActivity {

    String orderId;
    ImageButton backBtn, editBtn;
    TextView orderIdTv, dateTv, orderStatusTv, nameClientTv, addressTv ,phoneTv, amountTv;
    RecyclerView itemsRv;

    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelOrderItem> orderItemArrayList;
    private AdapterOrderedItem adapterOrderedItem;
    FirebaseFirestore db;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details_seller);

        //get data from intent
        orderId = getIntent().getStringExtra("orderId");

//        backBtn = findViewById(R.id.backBtn);
        editBtn = findViewById(R.id.editBtn);
        orderIdTv = findViewById(R.id.orderIdTv);
        dateTv = findViewById(R.id.dateTv);
        orderStatusTv = findViewById(R.id.orderStatusTv);
        nameClientTv = findViewById(R.id.nameClientTv);
        phoneTv = findViewById(R.id.phoneTv);
        addressTv = findViewById(R.id.addressTv);
        amountTv = findViewById(R.id.amountTv);
        itemsRv = findViewById(R.id.itemsRv);

        firebaseAuth = FirebaseAuth.getInstance();
        userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        loadBuyerInfo();
        loadOrderDetails();
        loadOrderItems();


//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //go back
//                onBackPressed();
//            }
//        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //edit order status
                editOrderStatusDialog();
            }
        });

    }


    private void editOrderStatusDialog() {
        // Options to display in dialog
        final String[] options = {"בתהליך", "בוטלה", "הושלמה"};

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("עריכת סטטוס הזמנה").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle items click
                String selectedOption = options[which];

                // Update order status text and color
                updateColorStatus(selectedOption);

                // Call method to further process the selected option
                editOrderStatus(selectedOption);
            }
        }).show();
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

    private void editOrderStatus(String selectedOption) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
//            String userID = user.getUid();
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
                                        updateProductsQuantity(docRef,userID);
                                    }

                                    // Update order status in the seller
                                    updateColorStatus(docRef, selectedOption);

                                    // Extract clientId and orderId from the seller's order document
                                    String clientId = documentSnapshot.getString("clientId");
                                    String orderId = documentSnapshot.getString("orderId");

                                    // Construct a query to find the client's order document
                                    assert clientId != null;
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
                                                updateColorStatus(clientOrderRef, selectedOption);
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

    private void updateProductsQuantity(DocumentReference orderDocRef, String sellerID) {
        //TODO
        Log.d("Debug", "in the updateProductsQuantity ");
        orderDocRef.collection("productsOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    WriteBatch batch = db.batch();

                    for (DocumentSnapshot productSnapshot : task.getResult()) {
                        String productId = productSnapshot.getString("productId");
                        String quantityString = productSnapshot.getString("productQuantity");
                        Log.d("Debug", "productId: " + productId);
                        Log.d("Debug", "quantityString: " + quantityString);

                        if (productId != null && quantityString != null) {
                            int quantityOrdered = Integer.parseInt(quantityString);
                            Log.d("Debug", "quantityOrdered: " + quantityOrdered);
                            // Update product quantity in the seller's collection
                            Query productQuery = db.collection("seller")
                                    .document(sellerID)
                                    .collection("products")
                                    .whereEqualTo("productId", productId);

                            productQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            DocumentReference productRef = document.getReference();
                                            int currentQuantity = Integer.parseInt(document.getString("productQuantity"));
                                            String updatedQuantity = String.valueOf(currentQuantity - quantityOrdered);
                                            Log.d("Debug", "currentQuantity: " + currentQuantity);
                                            Log.d("Debug", "updatedQuantity: " + updatedQuantity);

                                            Map<String, Object> updatedQuantityValue = new HashMap<>();
                                            updatedQuantityValue.put("productQuantity", updatedQuantity);

                                            // Update the quantity in the batch
                                            batch.update(productRef, updatedQuantityValue);
                                        }
                                        // Commit the batched write
                                        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "Batch write successful");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error committing batch write", e);
                                            }
                                        });
                                    } else {
                                        Log.e(TAG, "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                        }
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    private void updateColorStatus(DocumentReference docRef, String selectedOption) {
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
//        String userID = user.getUid();

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
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
//        String userID = user.getUid();

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
                            String orderId = documentSnapshot.getString("orderId");
                            String orderDate = documentSnapshot.getString("orderDate");
                            String orderStatus = documentSnapshot.getString("orderStatus");
                            String nameClient = documentSnapshot.getString("nameClient");
                            String orderAddress = documentSnapshot.getString("addressClient");
                            String phoneClient = documentSnapshot.getString("phoneClient");
                            String totalPrice = documentSnapshot.getString("totalPrice");

                            // Change order status text color
                            updateColorStatus(orderStatus);

                            // Display the fields
                            orderIdTv.setText(orderId);
                            dateTv.setText(orderDate);
                            orderStatusTv.setText(orderStatus);
                            nameClientTv.setText(nameClient);
                            addressTv.setText(orderAddress);
                            phoneTv.setText(phoneClient);
                            amountTv.setText(totalPrice);
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

        DocumentReference clientRef = db.collection("seller").document(userID);

        clientRef.collection("orders")
                .whereEqualTo("orderId", orderId) // Filter orders by orderId
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        orderItemArrayList.clear();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot orderDocument : task.getResult()) {

                                // Now, within the filtered order document, query the "products" sub-collection
                                orderDocument.getReference().collection("productsOrder").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> productsTask) {
                                        if (productsTask.isSuccessful()) {
                                            for (QueryDocumentSnapshot productDocument : productsTask.getResult()) {
                                                //get information from the product document
                                                String productId = productDocument.getString("productId");
                                                String productTitle = productDocument.getString("productTitle");
                                                String productPriceEach = productDocument.getString("productPriceEach");
                                                String productPrice = productDocument.getString("productPrice");
                                                String productQuantity = productDocument.getString("productQuantity");

                                                // Create ModelOrderItem object
                                                ModelOrderItem modelOrderItem = new ModelOrderItem(
                                                        productId,
                                                        productTitle,
                                                        productPrice,
                                                        productPriceEach,
                                                        productQuantity);
                                                orderItemArrayList.add(modelOrderItem);
                                            }
                                            // Setup adapter
                                            adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSeller.this, orderItemArrayList);
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
//
//    {
//        orderItemArrayList = new ArrayList<>();
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//
//
//        String userId = user.getUid();
//        DocumentReference sellerRef = db.collection("seller").document(userId);
//
//        sellerRef.collection("orders")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            QuerySnapshot querySnapshot = task.getResult();
//
//                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                                // Iterate over each document in the "orders" collection
//                                for (QueryDocumentSnapshot document : querySnapshot) {
//                                    ModelOrderItem modelOrderItem = document.toObject(ModelOrderItem.class);
//                                    orderItemArrayList.add(modelOrderItem);
//                                }
//                                adapterOrderedItem = new AdapterOrderedItem(OrderDetailsSeller.this, orderItemArrayList);
//                                itemsRv.setAdapter(adapterOrderedItem);
//
//                                //set total items in orders
//                                nameClientTv.setText(String.valueOf(querySnapshot.size()));
//                            } else {
//                                // Handle the case where the "orders" collection is empty
//                            }
//                        } else {
//                            // Handle potential errors here
//                            Exception exception = task.getException();
//                            if (exception != null) {
//                                // Log or handle the exception
//                            }
//                        }
//                    }
//                });
//
//    }
}