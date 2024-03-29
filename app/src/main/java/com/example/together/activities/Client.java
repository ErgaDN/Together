package com.example.together.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.together.Constants;
import com.example.together.R;
import com.example.together.adapters.AdapterCartItem;
//import com.example.together.adapters.AdapterOrderSeller;
import com.example.together.adapters.AdapterProductClient;
import com.example.together.models.ModelCartItem;
import com.example.together.models.ModelProduct;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class Client extends AppCompatActivity {
    public interface AddressCallback {
        void onAddressLoaded(String address);
    }
    ImageButton btn_profile, btn_client_cart, btn_logout, filterProductBtn;
    Toolbar toolbar;
    FirebaseFirestore db;
    private EditText searchProductsEt;
    private TextView filteredProductsTv, cartCountTv;
    private RecyclerView productsRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelProduct> productList;
    private AdapterProductClient adapterProductClient;
    private String userId;
    private int count;

    //cart
    private ArrayList<ModelCartItem> cartItemList;
    private AdapterCartItem adapterCartItem;
    private List<QueryDocumentSnapshot> sellersList;

    // prograss dialog
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        btn_profile = findViewById(R.id.btn_profile);
        btn_client_cart = findViewById(R.id.btn_client_cart);
        btn_logout = findViewById(R.id.btn_logout);
        searchProductsEt = findViewById(R.id.searchProductsEt);
        filteredProductsTv = findViewById(R.id.filteredProductsTv);
        filterProductBtn = findViewById(R.id.filterProductBtn);
        productsRv = findViewById(R.id.productsRv);
        cartCountTv = findViewById(R.id.cartCountTv);

        // init progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);


        firebaseAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        loadAllProducts();
        //search
        searchProductsEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    adapterProductClient.getFilter().filter(s);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        cartCount();

        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Client.this, ClientProfile.class);
                startActivity(intent);
                finish();
            }
        });

        btn_client_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCartDialog();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Client.this, Login.class);
                startActivity(intent);
                finish();
            }
        });

        filterProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Client.this);
                builder.setTitle("Choose Category:")
                        .setItems(Constants.productCategories_1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //get selected item
                                String selected = Constants.productCategories_1[which];
                                filteredProductsTv.setText(selected);
                                if (selected.equals("הכל")) {
                                    loadAllProducts();
                                }
                                else {
                                    adapterProductClient.getFilter().filter(selected);
                                }
                            }
                        })
                        .show();
            }
        });


    }

    public double allTotalPrice = 0.0;
    public RecyclerView cardItemRv;
    public TextView sTotalTv;
    public Button checkoutBtn;

    String myAddress , myNumber;
    String timestamp = "" + System.currentTimeMillis();

    //need to access these views in adapter so making public
    private void showCartDialog() {
        //init list
        cartItemList = new ArrayList<>();

        //inflate cart layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_cart, null);
        cardItemRv = view.findViewById(R.id.cardItemRv);
        sTotalTv = view.findViewById(R.id.sTotalTv);
        checkoutBtn = view.findViewById(R.id.checkoutBtn);

        DocumentReference userDocument = db.collection("clients").document(userId);

        userDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        // Check if the "Address" field exists in the document
                        if (document.contains("Address")) {
                            // Retrieve the "Address" field as a String
                            myAddress = document.getString("Address");
                        }
                        if (document.contains("Phone Number")) {
                            myNumber = document.getString("Phone Number");
                        }

                    }
                }
            }
        });


        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //set view to dialog
        builder.setView(view);


        checkoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // first validate delivery address
                if (myAddress.equals("") || myAddress.equals("null")) {
                    Toast.makeText(Client.this, "Please enter your address in your profile before placing order", Toast.LENGTH_SHORT).show();
                    return; // don't proceed further
                }
                if (myNumber.equals("") || myNumber.equals("null")) {
                    Toast.makeText(Client.this, "Please enter your phone in your profile before placing order", Toast.LENGTH_SHORT).show();
                    return; // don't proceed further
                }
                if (cartItemList.size() == 0) {
                    // cart list is empty
                    Toast.makeText(Client.this, "No item in cart", Toast.LENGTH_SHORT).show();
                }

                submitOrdersToSellers();
                submitOrder(); //add the order to DB under the orders collection
                deleteCartData(); //when confirm the order delete the products from the cart

                //open order details
                Intent intent = new Intent(Client.this, Client.class);
                startActivity(intent);
            }
        });

        DocumentReference docRef = db.collection("clients").document(userId);

        docRef.collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                cartItemList.clear();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot cartDocument : task.getResult()) {
                        //get information
                        String idProduct = cartDocument.getString("uid");
                        String pIdProduct = cartDocument.getString("productId");
                        String nameProduct = cartDocument.getString("productTitle");
                        String priceProduct = cartDocument.getString("productPriceEach");
                        String costProduct = cartDocument.getString("productPrice");
                        String quantityProduct = cartDocument.getString("productQuantity");

                        //update allTotalPrice
                        allTotalPrice += Double.parseDouble(costProduct);

                        // Use toObject to convert the document snapshot to a ModelProduct object
                        ModelCartItem modelCartItem = new ModelCartItem(
                                ""+idProduct,
                                ""+pIdProduct,
                                ""+nameProduct,
                                ""+priceProduct,
                                ""+costProduct,
                                ""+quantityProduct);

                        cartItemList.add(modelCartItem);

                    }
                    //setup adapter
                    adapterCartItem = new AdapterCartItem(Client.this, cartItemList);
                    //set adapter
                    cardItemRv.setAdapter(adapterCartItem);
                    sTotalTv.setText("₪" + allTotalPrice);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

        //reset total price on dialog dismiss
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                allTotalPrice = 0.0;
            }
        });

    }
    private void submitOrdersToSellers() {
        DocumentReference docRef = db.collection("clients").document(userId);

        docRef.collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Fetch client information only once
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> clientTask) {
                            if (clientTask.isSuccessful()) {
                                DocumentSnapshot clientDocument = clientTask.getResult();
                                if (clientDocument.exists()) {
                                    // Get client information
                                    String FirstNameClient = clientDocument.getString("First Name");
                                    String LastNameClient = clientDocument.getString("Last Name");
                                    String nameClient = FirstNameClient + " " + LastNameClient;
                                    String phoneClient = clientDocument.getString("Phone Number");
                                    String addressClient = clientDocument.getString("Address");

                                    double totalPrice = 0.0;

                                    // Create a map with the order information
                                    Map<String, Object> orderData = new HashMap<>();
                                    orderData.put("nameClient", nameClient);
                                    orderData.put("phoneClient", phoneClient);
                                    orderData.put("addressClient", addressClient);
                                    orderData.put("orderStatus", "בתהליך");
                                    orderData.put("orderId", "" + timestamp);
                                    orderData.put("clientId", "" + userId);
                                    Date orderDate = new Date(Long.parseLong(timestamp));
                                    DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US); // Using US locale for ASCII digits
                                    String formattedDate = dateFormat.format(orderDate);
                                    orderData.put("orderDate", formattedDate);

                                    for (QueryDocumentSnapshot cartDocument : task.getResult()) {
                                        // Get information from the cart item
                                        String productId = cartDocument.getString("productId");
                                        String productTitle = cartDocument.getString("productTitle");
                                        String productPriceEach = cartDocument.getString("productPriceEach");
                                        String productQuantity = cartDocument.getString("productQuantity");
                                        String sellerId = cartDocument.getString("sellerId");

                                        // Get the total price
                                        try {
                                            double price = Double.parseDouble(productPriceEach);
                                            double quantity = Double.parseDouble(productQuantity);
                                            double subtotal = price * quantity;
                                            totalPrice += subtotal;
                                            orderData.put("totalPrice", String.valueOf(totalPrice));
                                        } catch (NumberFormatException e) {
                                            Log.e("Error", "Failed to parse price or quantity: " + e.getMessage());
                                        }



                                        // Reference to the seller's "orders" sub-collection

                                        DocumentReference sellerOrdersRef = db.collection("seller").document(sellerId)
                                                .collection("orders").document(timestamp);

                                        // Add the order data to the order sub-collection
//                                        DocumentReference orderRef = sellerOrdersRef.document();
                                        sellerOrdersRef.set(orderData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Order data added successfully
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle failure to add order data
                                                    }
                                                });

                                        // Create a map with product information
                                        Map<String, Object> productData = new HashMap<>();
                                        productData.put("productId", productId);
                                        productData.put("productTitle", productTitle);
                                        productData.put("productPriceEach", productPriceEach);
                                        productData.put("productQuantity", productQuantity);


                                        // Add the product data to the order sub-collection
                                        sellerOrdersRef.collection("productsOrder").document(productId).set(productData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // Product data added successfully
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle failure to add product data
                                                    }
                                                });
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }




//    private void submitOrder() {
    private void deleteCartData() {
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        CollectionReference cartCollectionRef = mStore.collection("clients").document(userID).collection("cart");

        // Query all documents in the "cart" collection
        cartCollectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    // Create a batch to delete all documents
                    WriteBatch batch = mStore.batch();

                    // Iterate through each document and add a delete operation to the batch
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference docRef = cartCollectionRef.document(document.getId());
                        batch.delete(docRef);
                    }

                    // Commit the batch to delete all documents
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> batchTask) {
                            if (batchTask.isSuccessful()) {
                                // Documents successfully deleted
                                Toast.makeText(Client.this, "All documents in cart deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                // Handle the error if the batch commit fails
                                Toast.makeText(Client.this, "Failed to delete documents in cart", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }


    private void submitOrder() {
        // show progress dialog
        progressDialog.setMessage("מבצע הזמנה...");
        progressDialog.show();

        String cost = sTotalTv.getText().toString().trim().replace("₪", ""); // remove ₪ if contains
        String[] addressHolder = {"undefined"};
        Log.d(TAG, " before the method address = " + addressHolder[0]);

        loadUserAddress(userId, new AddressCallback() {
            @Override
            public void onAddressLoaded(String loadedAddress) {
                // Use the loaded address here
                Log.d(TAG, "Loaded address: " + loadedAddress);
                // Update the address variable
                addressHolder[0] = loadedAddress;

                updateAddressInFirestore(addressHolder[0]);


                // Setup order data
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("orderId", "" + timestamp);

                Date orderDate = new Date(Long.parseLong(timestamp));
                DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, Locale.US); // Using US locale for ASCII digits
                String formattedDate = dateFormat.format(orderDate);
                hashMap.put("orderDate", formattedDate);
//                hashMap.put("orderTime", "" + timestamp);
                hashMap.put("orderStatus", "בתהליך");
                hashMap.put("orderCost", "" + cost);
                hashMap.put("orderBy", "" + firebaseAuth.getUid());
                hashMap.put("address to delivery", "" + addressHolder[0]);

                // Add to db
                FirebaseFirestore mStore = FirebaseFirestore.getInstance();
                CollectionReference ordersCollectionRef = mStore.collection("clients").document(userId).collection("orders");

                // Add the sample order document to the "orders" collection
                ordersCollectionRef.add(hashMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                        String orderID = documentReference.getId();
                        CollectionReference productCollectionRef = mStore.collection("clients").document(userId).collection("orders")
                                .document(orderID).collection("products");

                        Toast.makeText(Client.this, "Added to orders!", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < cartItemList.size(); i++) {
                            String pId = cartItemList.get(i).getpId();
                            String id = cartItemList.get(i).getId();
                            String cost = cartItemList.get(i).getCost();
                            String name = cartItemList.get(i).getName();
                            String price = cartItemList.get(i).getPrice();
                            String quantity = cartItemList.get(i).getQuantity();

                            HashMap<String, String> hproducts = new HashMap<>();
                            hproducts.put("pId", pId);
                            hproducts.put("name", name);
                            hproducts.put("cost", cost);
                            hproducts.put("price", price);
                            hproducts.put("quantity", quantity);

                            productCollectionRef.add(hproducts).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressDialog.dismiss();
                                    Toast.makeText(Client.this, "Products added successfully", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Client.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateAddressInFirestore(String address) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference clientRef = db.collection("clients").document(userId);

        // Declare 'address' as final
        final String finalAddress = address;

        // Update the "Address" field in the client document
        clientRef.update("Address", finalAddress)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Address updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error updating address", e);
                    }
                });
    }


    private void loadUserAddress(String clientId, AddressCallback callback) {
        String[] address = {""}; // Use an array to make it effectively final

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference clientRef = db.collection("clients").document(userId);

        clientRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the "Address" field from the document
                        address[0] = document.getString("Address");
                    }
                }
                // Invoke the callback with the loaded address
                callback.onAddressLoaded(address[0]);
            }
        });
    }



    private void loadAllProducts() {

        productList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        sellersList = new ArrayList<>(); // Initialize sellersList

        // Create a reference to the "sellers" collection
        CollectionReference sellersRef = db.collection("seller");

        sellersRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                productList.clear();
                sellersList.clear(); // Clear the list before populating
                if (task.isSuccessful()) {
                    // Iterate over each seller document
                    for (QueryDocumentSnapshot sellerDocument : task.getResult()) {
                        sellersList.add(sellerDocument); // Add each seller to the list
                        String sellerId = sellerDocument.getId();

                        // Access the "products" subcollection inside the seller document
                        CollectionReference productsRef = sellersRef.document(sellerId).collection("products");

                        // Now you can perform operations on the "products" subcollection for this seller
                        productsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot productDocument : task.getResult()) {
                                        try {
                                            // Use toObject to convert the document snapshot to a ModelProduct object
                                            ModelProduct modelProduct = productDocument.toObject(ModelProduct.class);
                                            productList.add(modelProduct);
                                            Log.d("Debug", "all good with modelProduct");
                                            Log.d("Debug", "VVVVVVVVV");


                                        } catch (RuntimeException e) {

                                            Log.d("Debug", "problem with modelProduct");
                                            Log.d("Debug", "problem with: "+productDocument);
                                            Log.e("Firestore", "Error converting document to ModelProduct: " + e.getMessage());

                                        }
                                    }
                                    adapterProductClient = new AdapterProductClient(Client.this, productList);
                                    productsRv.setAdapter(adapterProductClient);
                                }
                            }
                        });
                    }
                }
            }
        });
    }


    private void cartCount(){
        DocumentReference docRef = db.collection("clients").document(userId);

        docRef.collection("cart").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                count=0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot cartDocument : task.getResult()) {
                        //get information
                        String quantityProduct = cartDocument.getString("productQuantity");

                        // Use toObject to convert the document snapshot to a ModelProduct object
                        try {
                            int quantity = Integer.parseInt(quantityProduct);
                            count += quantity;
                        } catch (NumberFormatException e) {
                            // Handle the case where the quantityProduct is not a valid integer
                            Log.e(TAG, "Error parsing quantity as integer", e);
                        }
                    }
                    cartCountTv.setVisibility(View.VISIBLE);
                    cartCountTv.setText(String.valueOf(count));
                }else {
                    cartCountTv.setVisibility(View.GONE);
                }
            }
        });
    }

}