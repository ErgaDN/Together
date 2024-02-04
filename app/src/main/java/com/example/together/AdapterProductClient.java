package com.example.together;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class AdapterProductClient extends RecyclerView.Adapter<AdapterProductClient.HolderProductClient> implements Filterable {
    private Context context;
    public ArrayList<ModelProduct> productsList, filterList;
    private FilterProductClient filter;


    public AdapterProductClient(Context context, ArrayList<ModelProduct> productsList) {
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_client, parent, false);
        return new HolderProductClient(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductClient holder, int position) {
        //get data
        ModelProduct modelProduct = productsList.get(position);
        String productCategory = modelProduct.getProductCategory();
        String productPrice = modelProduct.getProductPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String productTimestamp = modelProduct.getTimestamp();
        String productIcon = modelProduct.getProductIcon();

        //set data
        holder.titleTv.setText(productTitle);
        holder.descriptionTv.setText(productDescription);
        holder.priceTv.setText("₪" + productPrice);
        holder.addToCartTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //add product to cart
                showQuantityDialog(modelProduct);
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details
            }
        });
    }

    private double cost = 0;
    private double finalCost = 0;
    private int quantity = 0;
    private void showQuantityDialog(ModelProduct modelProduct) {
        //inflate layout for dialog
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_quantity, null);
        //init layout views
        ImageView productIv = view.findViewById(R.id.productIv);
        TextView titleTv = view.findViewById(R.id.titleTv);
        TextView pQuantityTv = view.findViewById(R.id.pQuantityTv);
        TextView descriptionTv = view.findViewById(R.id.descriptionTv);
        TextView originalPriceTv = view.findViewById(R.id.originalPriceTv);
        TextView finialPriceTv = view.findViewById(R.id.finialPriceTv);
        ImageButton decrementBtn = view.findViewById(R.id.decrementBtn);
        TextView quantityTv = view.findViewById(R.id.quantityTv);
        ImageButton incrementBtn = view.findViewById(R.id.incrementBtn);
        Button continueBtn = view.findViewById(R.id.continueBtn);

        //get data from model
        String productPrice = modelProduct.getProductPrice();
        String productDescription = modelProduct.getProductDescription();
        String productTitle = modelProduct.getProductTitle();
        String productQuantity = modelProduct.getProductQuantity();
        String productId = modelProduct.getProductId();
        String productIcon = modelProduct.getProductIcon();

        cost = Double.parseDouble(productPrice.replaceAll("₪", ""));
        finalCost = Double.parseDouble(productPrice.replaceAll("₪", ""));

        quantity =1;

        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        //set data
        try {
            Picasso.get().load(productIcon).placeholder(R.drawable.ic_shopping_cart_24).into(productIv);
        }catch (Exception e){
            productIv.setImageResource(R.drawable.ic_shopping_cart_24);
        }

        titleTv.setText(""+productTitle);
        pQuantityTv.setText(""+productQuantity);
        descriptionTv.setText(""+productDescription);
        quantityTv.setText(""+quantity);
        originalPriceTv.setText("₪"+modelProduct.getProductPrice());
        finialPriceTv.setText("₪"+finalCost);

        AlertDialog dialog = builder.create();
        dialog.show();

        //increase quantity of the product
        incrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalCost = finalCost + cost;
                quantity++;

                finialPriceTv.setText("₪"+ finalCost);
                quantityTv.setText(""+quantity);

            }
        });

        // decrement quantity of product, only if quantity>1
        decrementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity>1){
                    finalCost = finalCost-cost;
                    quantity--;

                    finialPriceTv.setText("₪"+finalCost);
                    quantityTv.setText(""+quantity);
                }
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleTv.getText().toString().trim();
                String priceEach = originalPriceTv.getText().toString().trim().replace("₪", "");
                String price = finialPriceTv.getText().toString().trim().replace("₪", "");
                String quantity = quantityTv.getText().toString().trim();
                String description = descriptionTv.getText().toString().trim();

                //add to DB
                addToCart(context, productId, title, priceEach, price, quantity, description);
                dialog.dismiss();
            }
        });
    }

    private void addToCart(Context context, String productId, String title, String priceEach, String price, String quantity, String description) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mStore = FirebaseFirestore.getInstance();
        //add data to db
        String timestamp = "" + System.currentTimeMillis();
        HashMap<String, Object> productData = new HashMap<>();
        productData.put("productId", "" + productId);
        productData.put("productTitle", "" + title);
        productData.put("productDescription", "" + description);
        productData.put("productQuantity", "" + quantity);
        productData.put("productPrice", "" + price);
        productData.put("productPriceEach", "" + priceEach);
        productData.put("timestamp", "" + timestamp);
        productData.put("uid", "" + firebaseAuth.getUid());
        //add to DB
        String userID = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        CollectionReference productCollectionRef = mStore.collection("clients").document(userID).collection("cart");

        // Add the sample product document to the "cart" collection
        productCollectionRef.add(productData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "Product added to the 'cart' collection under user: " + userID);
                Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, Client.class);
                context.startActivity(intent);
//                context.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProductClient(this, filterList);
        }
        return filter;
    }

    class HolderProductClient extends RecyclerView.ViewHolder {
        private ImageView productIconIv, nextIv;
        private TextView titleTv, descriptionTv, addToCartTv, priceTv;

        //uid views
        public HolderProductClient(@NonNull View itemView) {
            super(itemView);

            titleTv = itemView.findViewById(R.id.titleTv);
            descriptionTv = itemView.findViewById(R.id.descriptionTv);
            addToCartTv = itemView.findViewById(R.id.addToCartTv);
            priceTv = itemView.findViewById(R.id.priceTv);
            productIconIv = itemView.findViewById(R.id.productIconIv);
            nextIv = itemView.findViewById(R.id.nextIv);


        }
    }
}
