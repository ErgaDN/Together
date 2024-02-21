package com.example.together.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.together.FilterProduct;
import com.example.together.R;
import com.example.together.activities.ImageHelper;
import com.example.together.models.ModelProduct;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.Objects;

// Interface for image load listener
interface OnImageLoadListener {
    void onImageLoaded(String productIcon);
}

public class AdapterProductSeller extends RecyclerView.Adapter<AdapterProductSeller.HolderProductSeller> implements Filterable {

    private Context context;
    public ArrayList<ModelProduct> productList, filterList;
    private FilterProduct filter;

    public AdapterProductSeller(Context context, ArrayList<ModelProduct> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_product_seller, parent, false);
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        // Get data
        ModelProduct modelProduct = productList.get(position);
        String productId = modelProduct.getProductId();
        String productPrice = modelProduct.getProductPrice();
        String title = modelProduct.getProductTitle();
        String sellerId = modelProduct.getSellerId();

        // Set data
        holder.titleTv.setText(title);
        holder.priceTv.setText("₪" + productPrice);


        // TODO
        // Fetch and load product icon
        fetchProductIconFromFirestore(sellerId, productId, holder.productIconIv);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle item click, show item details
            }
        });
    }

    private void fetchProductIconFromFirestore(String sellerId, String productId, final ImageView imageView) {
        // Assuming you have a Firestore instance initialized already
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Constructing Firestore query to fetch the document where productId equals the provided productId

        Query query = db.collection("seller").document(sellerId)
                .collection("products").whereEqualTo("productId", productId);

        // Fetching productIcon
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // There should be only one document with matching productId
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    String productIcon = documentSnapshot.getString("productIcon");

                    // Assuming you have a method to load image from Base64 string into an ImageView
                    loadImageUsingBase64(productIcon, imageView);
                } else {
                    Log.d("DEBUG", "No such document");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("ERROR", "Error getting documents", e);
            }
        });
    }


    // Method to load image from Base64 string into an ImageView
    private void loadImageUsingBase64(String base64ImageString, ImageView imageView) {
        try {
            // Convert Base64 string to byte array
            byte[] decodedString = Base64.decode(base64ImageString, Base64.DEFAULT);

            // Decode the byte array into a Bitmap
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            // Set the decoded bitmap to the ImageView
            imageView.setImageBitmap(decodedByte);
        } catch (Exception e) {
            // Log any errors that occur during decoding or setting the bitmap
            Log.e("ERROR", "Error loading image using Base64", e);
        }
    }



    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterProduct(this, filterList);
        }
        return filter;
    }

    class HolderProductSeller extends RecyclerView.ViewHolder{

        //holds views of recyclerview
        private ImageView productIconIv;
        private TextView titleTv, priceTv;
        public HolderProductSeller(@NonNull View itemView) {
            super(itemView);

            productIconIv = itemView.findViewById(R.id.productIconIv);
            titleTv = itemView.findViewById(R.id.titleTv);
            priceTv = itemView.findViewById(R.id.priceTv);
        }
    }
}
