package com.example.together.activities;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.together.R;
import com.example.together.adapters.AdapterCartItem;
import com.example.together.adapters.AdapterOrderedItem;
import com.example.together.models.ModelCartItem;
import com.example.together.models.ModelOrderClient;
import com.example.together.models.ModelOrderItem;
import com.example.together.models.ModelOrderShop;
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
        String userID = user.getUid();
        CollectionReference docRef = db.collection("clients").document(userID).collection("orders");

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
                            String orderTime = documentSnapshot.getString("orderTime");
                            String orderStatus = documentSnapshot.getString("orderStatus");
                            String addressToDelivery = documentSnapshot.getString("address to delivery");
                            String orderCost = documentSnapshot.getString("orderCost");

                            // Display the fields
                            orderIdTv.setText(orderId);
                            dateTv.setText(orderTime);
                            orderStatusTv.setText(orderStatus);
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

    private ArrayList<ModelOrderItem> orderItemList;
    private AdapterOrderedItem adapterOrderedItem;
    private void loadOrderItem() {
        Log.d("Debug", "start loadOrderItem() ");
        orderItemList = new ArrayList<>();

        DocumentReference clientRef = db.collection("clients").document(userId);

        clientRef.collection("orders")
                .whereEqualTo("orderId", orderId) // Filter orders by orderId
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("Debug", "start onComplete() ");
                        orderItemList.clear();
                        if (task.isSuccessful()) {
                            Log.d("Debug", "start if 'orders' find");
                            for (QueryDocumentSnapshot orderDocument : task.getResult()) {
                                Log.d("Debug", "start each document in 'order' ");

                                // Now, within the filtered order document, query the "products" sub-collection
                                orderDocument.getReference().collection("products").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> productsTask) {
                                        if (productsTask.isSuccessful()) {
                                            Log.d("Debug", "start if 'products' find");
                                            for (QueryDocumentSnapshot productDocument : productsTask.getResult()) {
                                                Log.d("Debug", "start each document in 'products' ");
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
                                            Log.d("Debug", "after for() ");
                                            // Setup adapter
                                            adapterOrderedItem = new AdapterOrderedItem(OrderDetailsClient.this, orderItemList);
                                            Log.d("Debug", "after Setup adapter ");
                                            // Set adapter
                                            itemsRv.setAdapter(adapterOrderedItem);
                                            Log.d("Debug", "after Set adapter");
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




//    private void showCartDialog() {
//        //init list
//        orderItemList = new ArrayList<>();
//
//        //inflate cart layout
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
//        cardItemRv = view.findViewById(R.id.cardItemRv);
//        sTotalTv = view.findViewById(R.id.sTotalTv);
//        checkoutBtn = view.findViewById(R.id.checkoutBtn);
//
//        DocumentReference userDocument = db.collection("clients").document(userId);
//
//        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//
//                    if (document.exists()) {
//                        // Check if the "Address" field exists in the document
//                        if (document.contains("Address")) {
//                            // Retrieve the "Address" field as a String
//                            myAddress = document.getString("Address");
//                        }
//                        if (document.contains("Phone Number")) {
//                            myNumber = document.getString("Phone Number");
//                        }
//
//                    }
//                }
//            }
//        });
//
//
//        //dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        //set view to dialog
//        builder.setView(view);
//
//
//        checkoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // first validate delivery address
//                if (myAddress.equals("") || myAddress.equals("null")) {
//                    Toast.makeText(Client.this, "Please enter your address in your profile before placing order", Toast.LENGTH_SHORT).show();
//                    return; // don't proceed further
//                }
//                if (myNumber.equals("") || myNumber.equals("null")) {
//                    Toast.makeText(Client.this, "Please enter your phone in your profile before placing order", Toast.LENGTH_SHORT).show();
//                    return; // don't proceed further
//                }
//                if (orderItemList.size() == 0) {
//                    // cart list is empty
//                    Toast.makeText(Client.this, "No item in cart", Toast.LENGTH_SHORT).show();
//                }
//
//                submitOrdersToSellers();
//                submitOrder(); //add the order to DB under the orders collection
//                deleteCartData(); //when confirm the order delete the products from the cart
//
//                //open order details
//                Intent intent = new Intent(Client.this, Client.class);
//                startActivity(intent);
//            }
//        });
//
//        DocumentReference docRef = db.collection("clients").document(userId);
//
//        docRef.collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                orderItemList.clear();
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot cartDocument : task.getResult()) {
//                        //get information
//                        String idProduct = cartDocument.getString("uid");
//                        String pIdProduct = cartDocument.getString("productId");
//                        String nameProduct = cartDocument.getString("productTitle");
//                        String priceProduct = cartDocument.getString("productPriceEach");
//                        String costProduct = cartDocument.getString("productPrice");
//                        String quantityProduct = cartDocument.getString("productQuantity");
//
//                        //update allTotalPrice
//                        allTotalPrice += Double.parseDouble(costProduct);
//
//                        // Use toObject to convert the document snapshot to a ModelProduct object
//                        ModelCartItem modelCartItem = new ModelCartItem(
//                                ""+idProduct,
//                                ""+pIdProduct,
//                                ""+nameProduct,
//                                ""+priceProduct,
//                                ""+costProduct,
//                                ""+quantityProduct);
//
//                        orderItemList.add(modelCartItem);
//
//                    }
//                    //setup adapter
//                    adapterCartItem = new AdapterCartItem(Client.this, orderItemList);
//                    //set adapter
//                    cardItemRv.setAdapter(adapterCartItem);
//                    sTotalTv.setText("₪" + allTotalPrice);
//                }
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//
//        //reset total price on dialog dismiss
//        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                allTotalPrice = 0.0;
//            }
//        });
//
//    }
}