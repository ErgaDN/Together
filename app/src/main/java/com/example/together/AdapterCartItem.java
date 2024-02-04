//package com.example.together;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//
//public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.MolderCartItem>{
//
//    private Context context;
//    private ArrayList<MolderCartItem> cartItems;
//
//    public AdapterCartItem(Context context, ArrayList<MolderCartItem> cartItems) {
//        this.context = context;
//        this.cartItems = cartItems;
//    }
//
//    @NonNull
//    @Override
//    public MolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        //inflate layout row_cartitems.xml
//        View view = LayoutInflater.from(context).inflate(R.layout.row_cartitem, parent, false);
//        return new HolderCartItem(view); //TODO: ergo and ofir create this class
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MolderCartItem holder, int position) {
//        //get data
//        ModelCartItem modelCartItem = cartItems.get(position);
//        String id = modelCartItem.getId();
//        String getpid = modelCartItem.getpId();
//        String title = modelCartItem.getName();
//        String cost = modelCartItem.getCost();
//        String price = modelCartItem.getPrice();
//        String quantity = modelCartItem.getQuantity();
//
//        //set data
//        holder.itemTitleTv.setText(""+title);
//        holder.itemPriceTv.setText(""+cost);
//        holder.itemQuantityTv.setText("["+quantity+"]");
//
//        //handle remove click listener, delete item from cart
//        holder.itemRemoveTv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                EasyOB easyOB
//                //todo: show cart 19:38
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return cartItems.size(); //return number of records
//    }
//
//    //view holder class
//    class MolderCartItem extends RecyclerView.ViewHolder{
//
//
//        //ui view of row_cartitems.xml
//        private TextView itemTitleTv, itemPriceTv, itemQuantityTv, itemRemoveTv;
//        public MolderCartItem(@NonNull View itemView) {
//            super(itemView);
//
//            //init views
//            itemTitleTv = itemView.findViewById(R.id.itemTitleTv);
//            itemPriceTv = itemView.findViewById(R.id.itemPriceTv);
//            itemQuantityTv = itemView.findViewById(R.id.itemQuantityTv);
//            itemRemoveTv = itemView.findViewById(R.id.itemRemoveTv);
//
//        }
//    }
//}
