package com.example.together.adapters;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.together.R;
import com.example.together.models.ModelOrderItem;

import java.util.ArrayList;


public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{
    private Context context;
    private ArrayList<ModelOrderItem> orderItemArrayList;

    public AdapterOrderedItem(Context context, ArrayList<ModelOrderItem> orderItemArrayList) {
        this.context = context;
        this.orderItemArrayList = orderItemArrayList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_ordered_item, parent, false);
        return new HolderOrderedItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {
        //get data at position
        ModelOrderItem modelOrderItem = orderItemArrayList.get(position);
        String getpId = modelOrderItem.getpId();
        String name = modelOrderItem.getName();
        String cost = modelOrderItem.getCost();
        String price = modelOrderItem.getPrice();
        String quantity = modelOrderItem.getQuantity();

        //set data
        holder.itemTitleTv.setText(name);
        holder.itemPriceEachTv.setText("₪" + price);
        holder.itemPriceTv.setText("₪" + cost);
        holder.itemQuantityTv.setText("* " + quantity);
    }

    @Override
    public int getItemCount() {
        return orderItemArrayList.size();
    }

    //view holder class
    class HolderOrderedItem extends RecyclerView.ViewHolder{

        //views of row_orderd_item
        private TextView itemTitleTv, itemPriceTv, itemPriceEachTv, itemQuantityTv;

        public HolderOrderedItem(@NonNull View itemView) {
            super(itemView);

            //init views
            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
            itemPriceEachTv = itemView.findViewById(R.id.itemPriceEachTv);
            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
        }
    }
}
