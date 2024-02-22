package com.example.together.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.together.activities.OrderDetailsSeller;
import com.example.together.R;
import com.example.together.models.ModelOrderSeller;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterOrderSeller extends RecyclerView.Adapter<AdapterOrderSeller.HolderOrderShop> {
    FirebaseFirestore db;
    private Context context;
    public ArrayList<ModelOrderSeller> orderShopArrayList;

    class HolderOrderShop extends RecyclerView.ViewHolder{
        //ui views for row_order_selle.xml
        private final TextView orderIdTv, orderDateTv, amountTv, statusTv;
        private ImageView nextIv;
        public HolderOrderShop(@NonNull View itemView) {
            super(itemView);

            //init ui views
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }

    public AdapterOrderSeller(Context context, ArrayList<ModelOrderSeller> orderShopArrayList) {
        this.context = context;
        this.orderShopArrayList = orderShopArrayList;
    }

    @NonNull
    @Override
    public HolderOrderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);
        return new HolderOrderShop(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderShop holder, int position) {
//        get data at the position
        ModelOrderSeller modelOrderSeller = orderShopArrayList.get(position);
        String orderId = modelOrderSeller.getOrderId();

        String orderTotalCost = modelOrderSeller.getTotalPrice();
        String orderStatus = modelOrderSeller.getOrderStatus();
        String orderDate = modelOrderSeller.getOrderDate();

//        load user info
//        loadUserInfo(modelOrderSeller, holder);

        //set data
        holder.amountTv.setText("סכום: ₪"+ orderTotalCost);
        holder.orderIdTv.setText("מספר הזמנה: " + orderId);
        holder.orderDateTv.setText(orderDate);
        holder.statusTv.setText(orderStatus);
        //change order status text color
        switch (orderStatus) {
            case "בתהליך":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.lavender));
                break;
            case "הושלמה":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
                break;
            case "בוטלה":
                holder.statusTv.setTextColor(context.getResources().getColor(R.color.red));
                break;
        }

        //convert timestamp to proper format
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(orderTime));

        // Format the Calendar instance to a human-readable date string
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        //TODO: check if this 2 lines are necessary
//        String formattedDate = dateFormat.format(calendar.getTime());
//        holder.orderDateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details
                Intent intent = new Intent(context, OrderDetailsSeller.class);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });
    }


//    private void loadUserInfo(ModelOrderSeller modelOrderSeller, HolderOrderShop holder) {
//        // To load the client phone
//        String orderId = modelOrderSeller.getOrderId();
//        Log.d("Debug", "orderId"+ orderId);
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = mAuth.getCurrentUser();
//        String userId = user.getUid();
//        DocumentReference sellerRef = db.collection("seller").document(userId);
//
//        sellerRef.collection("orders")
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            // Now, the task.getResult() will return a QuerySnapshot
//                            QuerySnapshot querySnapshot = task.getResult();
//
//                            // Check if the querySnapshot is not null and contains any documents
//                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
//                                // Assuming you want to retrieve the first document
//                                DocumentSnapshot document = querySnapshot.getDocuments().get(0);
//
//                                // Retrieve the "Phone Number" field from the document
//                                String phoneNumber = document.getString("phoneClient");
//
//                                // Do something with the phoneNumber, for example, set it in your ViewHolder
//                                holder.phoneTv.setText("מספר טלפון: " + phoneNumber);
//                            } else {
//                                // Handle the case where the query result is empty
//                            }
//                        } else {
//                            // Handle potential errors here
//                        }
//                    }
//                });
//    }



    @Override
    public int getItemCount() {
        return orderShopArrayList.size();
    }

    //view holder class for row_order_selle.xml


}


// TODO: Add filter orders 29:24 (16)