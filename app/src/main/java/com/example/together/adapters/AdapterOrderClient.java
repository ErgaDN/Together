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

import com.example.together.activities.OrderDetailsClient;
import com.example.together.R;
import com.example.together.models.ModelOrderClient;

import java.util.ArrayList;

public class AdapterOrderClient extends RecyclerView.Adapter<AdapterOrderClient.HolderOrderClient> {
    private Context context;
    private ArrayList<ModelOrderClient> orderClientList;

    // view holder class
    class HolderOrderClient extends RecyclerView.ViewHolder {


        //views of layout
        private final TextView orderIdTv, orderDateTv, amountTv, statusTv;
        private ImageView nextIv;

        public HolderOrderClient(@NonNull View itemView) {
            super(itemView);

            //init ui views
            orderIdTv = itemView.findViewById(R.id.orderIdTv);
            orderDateTv = itemView.findViewById(R.id.orderDateTv);
            amountTv = itemView.findViewById(R.id.amountTv);
            statusTv = itemView.findViewById(R.id.statusTv);
            nextIv = itemView.findViewById(R.id.nextIv);
        }
    }

    public AdapterOrderClient(Context context, ArrayList<ModelOrderClient> orderClientList) {
        this.context = context;
        this.orderClientList = orderClientList;
    }

    @NonNull
    @Override
    public HolderOrderClient onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_order, parent, false);
        return new HolderOrderClient(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderClient holder, int position) {
        //get data
        ModelOrderClient modelOrderClient = orderClientList.get(position);
        String orderId = modelOrderClient.getOrderId();

        String orderTotalCost = modelOrderClient.getOrderCost();
        String orderStatus = modelOrderClient.getOrderStatus();
        String orderDate = modelOrderClient.getOrderDate();

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
        //TODO: need to convert timestamp to proper format. this code fail the running whene the client enter into HistoryOrders
        //convert timestamp to proper format
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(Long.parseLong(orderDate));

        // Format the Calendar instance to a human-readable date string
//        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
//        String formattedDate = dateFormat.format(calendar.getTime());

//        holder.dateTv.setText(formattedDate);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open order details
                Intent intent = new Intent(context, OrderDetailsClient.class);
                intent.putExtra("orderId", orderId);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return orderClientList.size();
    }

}
