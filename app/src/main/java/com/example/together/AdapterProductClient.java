package com.example.together;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
            }

        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show product details
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
