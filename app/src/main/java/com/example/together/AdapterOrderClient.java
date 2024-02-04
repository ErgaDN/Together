package com.example.together;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.text.SimpleDateFormat;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AdapterOrderClient extends RecyclerView.Adapter<AdapterOrderClient.HolderOrderClient> {
    private Context context;
    private ArrayList<ModelOrderClient> orderClientList;

    public AdapterOrderClient(Context context, ArrayList<ModelOrderClient> orderClientList) {
        this.context = context;
        this.orderClientList = orderClientList;
    }

    @NonNull
    @Override
    public HolderOrderClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_client, parent, false);
        return new HolderOrderClient(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderClient holder, int position) {
        //get data
        ModelOrderClient modelOrderClient = orderClientList.get(position);
        String orderId = modelOrderClient.getOrderId();
        String orderBy = modelOrderClient.getOrderBy();
        String orderCost = modelOrderClient.getOrderCost();
        String orderStatus = modelOrderClient.getOrderStatus();
        String orderTime = modelOrderClient.getOrderTime();


        // set data
        holder.amountTv.setText("סכום ₪:" + orderCost);
        holder.statusTv.setText(orderStatus);
        holder.orderIdTv.setText("OrderID: "+ orderId);

        //change order status text color
        if (orderStatus.equals("בתהליך")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.lavender));
        } else if (orderStatus.equals("הושלמה")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.green));
        } else if (orderStatus.equals("בוטלה")) {
            holder.statusTv.setTextColor(context.getResources().getColor(R.color.red));
        }

        //convert timestamp to proper format
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(orderTime));

        // Format the Calendar instance to a human-readable date string
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(calendar.getTime());

        holder.dateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details
                Intent intent = new Intent(context, OrderDeatailsClient.class);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderClientList.size();
    }


    // view holder class
    class HolderOrderClient extends RecyclerView.ViewHolder {


        //views of layout
        private TextView orderIdTv, dateTv, shopNameTv, amountTv, statusTv;

        public HolderOrderClient(@NonNull View itemView) {
            super(itemView);
            // init views of layout
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            dateTv = itemView.findViewById(R.id.dateTv);
            shopNameTv = itemView.findViewById(R.id.shopNameTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
        }
    }
}
